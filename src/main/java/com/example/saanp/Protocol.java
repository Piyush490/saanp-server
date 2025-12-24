package com.example.saanp;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

public class Protocol {

    public static void broadcast(GameRoom room) {
        JsonObject root = new JsonObject();
        root.addProperty("type", "snapshot");

        JsonObject data = new JsonObject();

        // Players
        JsonArray playersArray = new JsonArray();
        for (Player p : room.getPlayers()) {
            JsonObject po = new JsonObject();
            po.addProperty("id", p.channel.id().asShortText());
            po.addProperty("x", p.snake.x);
            po.addProperty("y", p.snake.y);
            po.addProperty("angle", p.snake.angle);
            po.addProperty("radius", p.snake.radius);
            po.addProperty("dead", p.snake.dead);
            po.addProperty("color", p.color);
            playersArray.add(po);

            // If player just died, send a gameOver message to them specifically
            if (p.snake.dead && !p.gameOverSent) {
                sendGameOver(p);
                p.gameOverSent = true;
            }
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

        // Broadcast to all players
        for (Player p : room.getPlayers()) {
            if (!p.channel.isActive()) continue;
            p.channel.writeAndFlush(new TextWebSocketFrame(json));
        }
    }

    private static void sendGameOver(Player p) {
        JsonObject root = new JsonObject();
        root.addProperty("type", "gameOver");
        
        JsonObject data = new JsonObject();
        data.addProperty("score", (int) p.snake.radius);
        root.add("data", data);

        p.channel.writeAndFlush(new TextWebSocketFrame(root.toString()));
    }
}
