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

        // 🟢 Collision Logic
        for (Player p : players) {
            if (p.snake.dead) continue;
            
            for (Player other : players) {
                if (p == other || other.snake.dead) continue;
                if (checkHeadToAnyCollision(p.snake, other.snake)) {
                    p.snake.dead = true;
                    deadPlayers.add(p);
                    break;
                }
            }
            if (p.snake.dead) continue;

            for (Bot b : bots) {
                if (b.snake.dead) continue;
                if (checkHeadToAnyCollision(p.snake, b.snake)) {
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
                if (checkHeadToAnyCollision(b.snake, p.snake)) {
                    b.snake.dead = true;
                    deadBots.add(b);
                    break;
                }
            }
            if (b.snake.dead) continue;

            for (Bot other : bots) {
                if (b == other || other.snake.dead) continue;
                if (checkHeadToAnyCollision(b.snake, other.snake)) {
                    b.snake.dead = true;
                    deadBots.add(b);
                    break;
                }
            }
        }

        foods.removeAll(foodToRemove);

        for (Player p : deadPlayers) {
            dropFood(p.snake, foods);
        }
        for (Bot b : deadBots) {
            dropFood(b.snake, foods);
            bots.remove(b);
        }
    }

    private static void checkFood(Snake s, List<Food> foods, List<Food> toRemove) {
        // Mouth distance for magnet effect: how far the snake can "reach"
        float mouthReach = s.radius * 2.5f; 
        
        for (Food f : foods) {
            float dx = s.x - f.x;
            float dy = s.y - f.y;
            float dSq = dx * dx + dy * dy;
            
            // If food is within reach, it's considered eaten
            if (dSq < mouthReach * mouthReach) {
                s.score++;
                
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

    private static boolean checkHeadToAnyCollision(Snake head, Snake other) {
        float dx = head.x - other.x;
        float dy = head.y - other.y;
        float distSq = dx * dx + dy * dy;
        float collisionDist = head.radius + other.radius;
        return distSq < (collisionDist * collisionDist) * 0.95f;
    }

    private static void dropFood(Snake s, List<Food> foods) {
        for (int i = 0; i < s.score; i++) {
            foods.add(new Food(
                    s.x + (float) (Math.random() * 120 - 60),
                    s.y + (float) (Math.random() * 120 - 60),
                    1
            ));
        }
    }
}
