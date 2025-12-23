package com.example.saanp;

import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

public class Protocol {

    public static void broadcast(GameRoom room) {
        StringBuilder sb = new StringBuilder();

        sb.append("P|");
        for (Player p : room.getPlayers()) {
            sb.append(p.snake.x).append(",")
                    .append(p.snake.y).append(",")
                    .append(p.snake.radius).append(";");
        }

        sb.append("|F|");
        for (Food f : room.getFoods()) {
            sb.append(f.x).append(",")
                    .append(f.y).append(";");
        }

        String msg = sb.toString();
        for (Player p : room.getPlayers()) {
            p.channel.writeAndFlush(new TextWebSocketFrame(msg));
        }
    }
}
