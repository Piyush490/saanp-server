package com.example.saanp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CollisionSystem {

    public static void resolve(Collection<Player> players, List<Bot> bots, List<Food> foods) {

        List<Food> foodToRemove = new ArrayList<>();
        List<Player> deadPlayers = new ArrayList<>();
        List<Bot> deadBots = new ArrayList<>();

        // 🟢 Everyone eats food
        for (Player p : players) {
            if (p.snake.dead) continue;
            checkFood(p.snake, foods, foodToRemove);
        }
        for (Bot b : bots) {
            if (b.snake.dead) continue;
            checkFood(b.snake, foods, foodToRemove);
        }

        // 🟢 Collision Logic (Head-to-Body)
        for (Player p : players) {
            if (p.snake.dead) continue;
            
            for (Player other : players) {
                if (p == other || other.snake.dead) continue;
                if (checkHeadToBodyCollision(p.snake, other.snake)) {
                    p.snake.dead = true;
                    deadPlayers.add(p);
                    break;
                }
            }
            if (p.snake.dead) continue;

            for (Bot b : bots) {
                if (b.snake.dead) continue;
                if (checkHeadToBodyCollision(p.snake, b.snake)) {
                    p.snake.dead = true;
                    deadPlayers.add(p);
                    break;
                }
            }
        }

        for (Bot b : bots) {
            if (b.snake.dead) continue;

            for (Player p : players) {
                if (p.snake.dead) continue;
                if (checkHeadToBodyCollision(b.snake, p.snake)) {
                    b.snake.dead = true;
                    deadBots.add(b);
                    break;
                }
            }
            if (b.snake.dead) continue;

            for (Bot other : bots) {
                if (b == other || other.snake.dead) continue;
                if (checkHeadToBodyCollision(b.snake, other.snake)) {
                    b.snake.dead = true;
                    deadBots.add(b);
                    break;
                }
            }
        }

        foods.removeAll(foodToRemove);

        // 🛑 IMPORTANT: Do not close the channel here. 
        // We need to keep it open long enough to send the "gameOver" message in Protocol.broadcast()
        for (Player p : deadPlayers) {
            dropFood(p.snake, foods);
        }
        for (Bot b : deadBots) {
            dropFood(b.snake, foods);
            bots.remove(b);
        }
    }

    private static void checkFood(Snake s, List<Food> foods, List<Food> toRemove) {
        for (Food f : foods) {
            float dx = s.x - f.x;
            float dy = s.y - f.y;
            if (dx * dx + dy * dy < s.radius * s.radius) {
                float growthFactor = 0.1f;
                if (s.radius > 30) growthFactor = 0.05f;
                if (s.radius > 50) growthFactor = 0.02f;
                if (s.radius > 80) growthFactor = 0.01f;
                
                if (s.radius < 100) {
                    s.radius += growthFactor; 
                }
                toRemove.add(f);
            }
        }
    }

    private static boolean checkHeadToBodyCollision(Snake head, Snake body) {
        float dx = head.x - body.x;
        float dy = head.y - body.y;
        float distSq = dx * dx + dy * dy;
        float combinedRadius = head.radius + body.radius;
        return distSq < (combinedRadius * combinedRadius) * 0.7f;
    }

    private static void dropFood(Snake s, List<Food> foods) {
        int foodCount = (int) (s.radius / 4);
        for (int i = 0; i < foodCount; i++) {
            foods.add(new Food(
                    s.x + (float) (Math.random() * 80 - 40),
                    s.y + (float) (Math.random() * 80 - 40),
                    1
            ));
        }
    }
}
