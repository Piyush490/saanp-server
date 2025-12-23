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
        JsonArray players = new JsonArray();
        for (Player p : room.getPlayers()) {

            JsonObject po = new JsonObject();
            po.addProperty("id", p.channel.id().asShortText());
            po.addProperty("x", p.snake.x);
            po.addProperty("y", p.snake.y);
            po.addProperty("angle", p.snake.angle);
            po.addProperty("radius", p.snake.radius);
            po.addProperty("dead", p.snake.dead);
            po.addProperty("color", p.color);

            players.add(po);
        }

        // Food
        JsonArray food = new JsonArray();
        for (Food f : room.getFoods()) {

            JsonObject fo = new JsonObject();
            fo.addProperty("x", f.x);
            fo.addProperty("y", f.y);

            food.add(fo);
        }

        data.add("players", players);
        data.add("food", food);

        root.add("data", data);

        String json = root.toString();

        // Broadcast to all players
        for (Player p : room.getPlayers()) {
            p.channel.writeAndFlush(new TextWebSocketFrame(json));
        }
    }
}
