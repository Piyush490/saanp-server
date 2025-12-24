package com.example.saanp;

import io.netty.channel.Channel;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class GameRoom {

    private static final GameRoom INSTANCE = new GameRoom();

    public static GameRoom getInstance() {
        return INSTANCE;
    }

    private static final int MAX_FOOD = 300;
    public static final float MAP_SIZE = 10000f; // Total width/height
    public static final float MAP_RADIUS = 5000f; // Radius from center

    private final Map<String, Player> players = new ConcurrentHashMap<>();
    private final List<Food> foods = new CopyOnWriteArrayList<>();
    private final GameLoop loop = new GameLoop(this);

    // 🔒 private constructor
    private GameRoom() {
        spawnInitialFood();
        loop.start();
    }

    private void spawnInitialFood() {
        while (foods.size() < MAX_FOOD) {
            foods.add(randomFood());
        }
    }

    private Food randomFood() {
        // Random point inside circular map
        double angle = Math.random() * Math.PI * 2;
        double radius = Math.sqrt(Math.random()) * MAP_RADIUS;
        float x = (float) (MAP_RADIUS + Math.cos(angle) * radius);
        float y = (float) (MAP_RADIUS + Math.sin(angle) * radius);
        
        return new Food(x, y, 1);
    }

    public void addPlayer(Player p) {
        String id = p.channel.id().asShortText();

        if (players.containsKey(id)) {
            System.out.println("[ROOM] Player already exists: " + id);
            return;
        }

        players.put(id, p);
        System.out.println("[ROOM] Player added. totalPlayers=" + players.size());
    }

    public void removePlayerByChannel(Channel channel) {
        String id = channel.id().asShortText();
        players.remove(id);
        System.out.println("[ROOM] Player removed. totalPlayers=" + players.size());
    }

    public Collection<Player> getPlayers() {
        return players.values();
    }

    public List<Food> getFoods() {
        return foods;
    }

    public void update() {

        // 1️⃣ Update player movement
        for (Player p : players.values()) {
            p.snake.update(p.inputAngle, p.boosting);
        }

        // 2️⃣ Resolve collisions
        CollisionSystem.resolve(players.values(), foods);

        // 3️⃣ Refill food
        while (foods.size() < MAX_FOOD) {
            foods.add(randomFood());
        }
    }
    public void removeInactivePlayers(long now) {

        players.values().removeIf(p -> {
            boolean inactive =
                    !p.channel.isActive() &&
                            now - p.lastSeen > 10_000;

            if (inactive) {
                System.out.println(
                        "[ROOM] Removing inactive player " +
                                p.channel.id().asShortText()
                );
            }

            return inactive;
        });
    }

}
