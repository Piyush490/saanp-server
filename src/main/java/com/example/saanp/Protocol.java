package com.example.saanp;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

public class Protocol {

    public static void broadcast(GameRoom room) {
        JsonObject root = new JsonObject();
        root.addProperty("type", "snapshot");

        JsonObject data = new JsonObject();

        // Players + Bots merged into one array for the client
        JsonArray playersArray = new JsonArray();
        
        // Add human players
        for (Player p : room.getPlayers()) {
            if (p.snake == null) continue;
            JsonObject po = new JsonObject();
            po.addProperty("id", p.channel.id().asShortText());
            po.addProperty("name", p.name);
            po.addProperty("x", p.snake.x);
            po.addProperty("y", p.snake.y);
            po.addProperty("angle", p.snake.angle);
            po.addProperty("radius", p.snake.radius);
            po.addProperty("score", p.snake.score); // Use snake.score directly
            po.addProperty("boosting", p.boosting);
            po.addProperty("dead", p.snake.dead);
            po.addProperty("color", p.color);
            playersArray.add(po);

            if (p.snake.dead && !p.gameOverSent) {
                sendGameOver(p);
                p.gameOverSent = true;
            }
        }

        // Add bots
        for (Bot b : room.getBots()) {
            if (b.snake == null) continue;
            JsonObject bo = new JsonObject();
            bo.addProperty("id", b.id);
            bo.addProperty("name", b.name);
            bo.addProperty("x", b.snake.x);
            bo.addProperty("y", b.snake.y);
            bo.addProperty("angle", b.snake.angle);
            bo.addProperty("radius", b.snake.radius);
            bo.addProperty("score", b.snake.score); // Use snake.score directly
            bo.addProperty("boosting", false);
            bo.addProperty("dead", b.snake.dead);
            bo.addProperty("color", b.color);
            playersArray.add(bo);
        }

        // Food
        JsonArray foodArray = new JsonArray();
        for (Food f : room.getFoods()) {
            JsonObject fo = new JsonObject();
            fo.addProperty("x", f.x);
            fo.addProperty("y", f.y);
            foodArray.add(fo);
        }

        data.add("players", playersArray);
        data.add("food", foodArray);
        root.add("data", data);

        String json = root.toString();

        // Broadcast to all active human players
        for (Player p : room.getPlayers()) {
            if (p.channel == null || !p.channel.isActive()) continue;
            p.channel.writeAndFlush(new TextWebSocketFrame(json));
        }
    }

    private static void sendGameOver(Player p) {
        JsonObject root = new JsonObject();
        root.addProperty("type", "gameOver");
        
        JsonObject data = new JsonObject();
        data.addProperty("score", p.snake.score); // Use snake.score directly
        root.add("data", data);

        if (p.channel != null && p.channel.isActive()) {
            p.channel.writeAndFlush(new TextWebSocketFrame(root.toString()));
        }
    }
}
