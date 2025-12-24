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
        // Check Players
        for (Player p : players) {
            if (p.snake.dead) continue;
            
            // vs other players
            for (Player other : players) {
                if (p == other || other.snake.dead) continue;
                if (checkHeadToBodyCollision(p.snake, other.snake)) {
                    p.snake.dead = true;
                    deadPlayers.add(p);
                    break;
                }
            }
            if (p.snake.dead) continue;

            // vs bots
            for (Bot b : bots) {
                if (b.snake.dead) continue;
                if (checkHeadToBodyCollision(p.snake, b.snake)) {
                    p.snake.dead = true;
                    deadPlayers.add(p);
                    break;
                }
            }
        }

        // Check Bots
        for (Bot b : bots) {
            if (b.snake.dead) continue;

            // vs players
            for (Player p : players) {
                if (p.snake.dead) continue;
                if (checkHeadToBodyCollision(b.snake, p.snake)) {
                    b.snake.dead = true;
                    deadBots.add(b);
                    break;
                }
            }
            if (b.snake.dead) continue;

            // vs other bots
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

        for (Player p : deadPlayers) {
            dropFood(p.snake, foods);
            p.channel.close(); 
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
            // Eat food if head overlaps it
            if (dx * dx + dy * dy < s.radius * s.radius) {
                // Diminishing growth: The fatter you are, the less you grow per food.
                // This keeps width from growing too much while still allowing long snakes.
                float growthFactor = 0.1f; // Base growth
                if (s.radius > 30) growthFactor = 0.05f;
                if (s.radius > 50) growthFactor = 0.02f;
                if (s.radius > 80) growthFactor = 0.01f;
                
                if (s.radius < 100) { // Absolute cap for width
                    s.radius += growthFactor; 
                }
                toRemove.add(f);
            }
        }
    }

    /**
     * Proper Head-to-Body collision detection.
     * Snake 'head' dies if it hits the 'body' snake.
     */
    private static boolean checkHeadToBodyCollision(Snake head, Snake body) {
        float dx = head.x - body.x;
        float dy = head.y - body.y;
        float distSq = dx * dx + dy * dy;
        
        // Sum of radii squared
        float combinedRadius = head.radius + body.radius;
        // Collision if the circles overlap significantly
        return distSq < (combinedRadius * combinedRadius) * 0.7f;
    }

    private static void dropFood(Snake s, List<Food> foods) {
        // Drop food based on size, but limited
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
