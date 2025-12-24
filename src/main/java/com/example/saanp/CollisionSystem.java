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
            if (isHeadCollidingWithAnyBody(p.snake, allSnakes)) {
                p.snake.dead = true;
                deadPlayers.add(p);
            }
        }

        // Check Bots for death
        for (Bot b : bots) {
            if (b.snake.dead) continue;
            if (isHeadCollidingWithAnyBody(b.snake, allSnakes)) {
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
     * Proper Slither-style collision: 
     * A snake dies if its HEAD touches ANY part of another snake's BODY.
     */
    private static boolean isHeadCollidingWithAnyBody(Snake headSnake, List<Snake> allSnakes) {
        for (Snake otherSnake : allSnakes) {
            // Can't die by touching your own head, but you CAN die by touching your own body 
            // if you make a sharp U-turn (like real slither).
            
            // Iterate through the other snake's body segments
            // We skip the first few segments of the head itself to prevent accidental self-collision
            int startIndex = (headSnake == otherSnake) ? 10 : 0;
            
            for (int i = startIndex; i < otherSnake.segments.size(); i++) {
                Snake.Point segment = otherSnake.segments.get(i);
                
                float dx = headSnake.x - segment.x;
                float dy = headSnake.y - segment.y;
                float distSq = dx * dx + dy * dy;
                
                // collisionDist: Sum of radii (head radius + body segment radius)
                float collisionDist = headSnake.radius + otherSnake.radius;
                
                // If head overlaps with any body segment
                if (distSq < (collisionDist * collisionDist) * 0.8f) {
                    return true;
                }
            }
        }
        return false;
    }

    private static void dropFood(Snake s, List<Food> foods) {
        int count = Math.min(s.score, 150); 
        for (int i = 0; i < count; i++) {
            foods.add(new Food(
                    s.x + (float) (Math.random() * 150 - 75),
                    s.y + (float) (Math.random() * 150 - 75),
                    1
            ));
        }
    }
}
