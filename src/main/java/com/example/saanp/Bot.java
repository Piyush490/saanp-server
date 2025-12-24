package com.example.saanp;

import java.util.List;
import java.util.UUID;

public class Bot {
    public final String id;
    public final String name;
    public final int color;
    public final Snake snake;
    
    private float targetAngle;
    private long lastAiUpdate = 0;

    public Bot(String name, int color) {
        this.id = "bot_" + UUID.randomUUID().toString().substring(0, 8);
        this.name = name;
        this.color = color;
        this.snake = new Snake();
        this.targetAngle = (float) (Math.random() * Math.PI * 2);
    }

    public void update(List<Food> foods, List<Player> players, List<Bot> otherBots) {
        long now = System.currentTimeMillis();
        if (now - lastAiUpdate > 200) { // Update AI every 200ms
            lastAiUpdate = now;
            decideDirection(foods, players, otherBots);
        }
        snake.update(targetAngle, false);
    }

    private void decideDirection(List<Food> foods, List<Player> players, List<Bot> otherBots) {
        // 1. Avoid Boundaries
        float dx = snake.x - GameRoom.MAP_RADIUS;
        float dy = snake.y - GameRoom.MAP_RADIUS;
        float distFromCenter = (float) Math.sqrt(dx * dx + dy * dy);
        
        if (distFromCenter > GameRoom.MAP_RADIUS * 0.8f) {
            // Turn back towards center
            targetAngle = (float) Math.atan2(-dy, -dx);
            return;
        }

        // 2. Find nearest food
        Food nearest = null;
        float minDist = Float.MAX_VALUE;
        for (Food f : foods) {
            float fdx = f.x - snake.x;
            float fdy = f.y - snake.y;
            float d2 = fdx * fdx + fdy * fdy;
            if (d2 < minDist) {
                minDist = d2;
                nearest = f;
            }
        }

        if (nearest != null && minDist < 1000 * 1000) {
            targetAngle = (float) Math.atan2(nearest.y - snake.y, nearest.x - snake.x);
        }
    }
}
