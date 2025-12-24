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

        // 🟢 Collision Logic (Combined Players + Bots)
        List<Snake> allSnakes = new ArrayList<>();
        for (Player p : players) if (!p.snake.dead) allSnakes.add(p.snake);
        for (Bot b : bots) if (!b.snake.dead) allSnakes.add(b.snake);

        // Check Players for death
        for (Player p : players) {
            if (p.snake.dead) continue;
            // Only check head-to-body collisions, ignore head-to-self
            if (checkSnakeDeath(p.snake, allSnakes)) {
                p.snake.dead = true;
                deadPlayers.add(p);
            }
        }

        // Check Bots for death
        for (Bot b : bots) {
            if (b.snake.dead) continue;
            if (checkSnakeDeath(b.snake, allSnakes)) {
                b.snake.dead = true;
                deadBots.add(b);
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
        float mouthReach = s.radius * 2.5f; 
        for (Food f : foods) {
            float dx = s.x - f.x;
            float dy = s.y - f.y;
            if (dx * dx + dy * dy < mouthReach * mouthReach) {
                s.score++;
                float growthFactor = 0.1f;
                if (s.radius > 30) growthFactor = 0.05f;
                if (s.radius > 50) growthFactor = 0.02f;
                if (s.radius > 80) growthFactor = 0.01f;
                if (s.radius < 100) s.radius += growthFactor; 
                toRemove.add(f);
            }
        }
    }

    /**
     * Accurately check if a snake's head has collided with another snake's body.
     */
    private static boolean checkSnakeDeath(Snake head, List<Snake> allSnakes) {
        for (Snake other : allSnakes) {
            // NEVER die by touching yourself
            if (head == other) continue;
            
            float dx = head.x - other.x;
            float dy = head.y - other.y;
            float distSq = dx * dx + dy * dy;
            
            // Collision occurs when the head overlaps with another snake's body
            // We use a tight threshold (0.8 of combined radii) to avoid accidental deaths
            float collisionDist = head.radius + other.radius;
            if (distSq < (collisionDist * collisionDist) * 0.65f) {
                return true;
            }
        }
        return false;
    }

    private static void dropFood(Snake s, List<Food> foods) {
        // Drop food proportional to size, but not overwhelming
        int count = Math.min(s.score, 100); 
        for (int i = 0; i < count; i++) {
            foods.add(new Food(
                    s.x + (float) (Math.random() * 150 - 75),
                    s.y + (float) (Math.random() * 150 - 75),
                    1
            ));
        }
    }
}
