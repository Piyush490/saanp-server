package com.example.saanp;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

public class WebSocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private final GameRoom gameRoom = GameRoom.getInstance();
    private Player player;

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        System.out.println("Client connected: " + ctx.channel().id());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        if (player != null) {
            gameRoom.removePlayerByChannel(ctx.channel());
        }
        System.out.println("Client disconnected: " + ctx.channel().id());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) {
        if (player != null) {
            player.lastSeen = System.currentTimeMillis();
        }

        String text = msg.text().trim();
        if (!text.startsWith("{")) return;

        try {
            JsonObject json = JsonParser.parseString(text).getAsJsonObject();
            String type = json.get("type").getAsString();
            JsonObject data = json.getAsJsonObject("data");

            switch (type) {

                case "join":
                    if (player != null) return;

                    String name = data.get("name").getAsString();
                    int color = data.get("color").getAsInt();

                    player = new Player(ctx.channel(), name, color);
                    gameRoom.addPlayer(player);

                    // Send confirmation with ID back to client
                    JsonObject welcome = new JsonObject();
                    welcome.addProperty("type", "joined");
                    JsonObject welcomeData = new JsonObject();
                    welcomeData.addProperty("id", ctx.channel().id().asShortText());
                    welcome.add("data", welcomeData);
                    ctx.channel().writeAndFlush(new TextWebSocketFrame(welcome.toString()));
                    break;

                case "input":
                    if (player != null) {
                        player.inputAngle = data.get("angle").getAsFloat();
                        player.boosting = data.get("boosting").getAsBoolean();
                    }
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
