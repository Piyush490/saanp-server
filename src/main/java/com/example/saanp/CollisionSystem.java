package com.example.saanp;

import java.util.*;

public class CollisionSystem {

    // Tunables to get a Slither-like feel
    private static final float BODY_THICKNESS_FACTOR = 0.85f; // how "thick" body feels for collision
    private static final int SELF_COLLISION_SKIP_SEGMENTS = 15; // head can't hit first N of its own segments
    private static final float HEAD_TO_HEAD_FACTOR = 0.9f; // tighter threshold for head-to-head

    public static void resolve(Collection<Player> players, List<Bot> bots, List<Food> foods) {

        // Food updates
        Set<Food> foodToRemove = new HashSet<>(); // avoid duplicates
        List<Player> deadPlayers = new ArrayList<>();
        List<Bot> deadBots = new ArrayList<>();

        // Everyone eats food
        for (Player p : players) {
            if (p.snake.dead) continue;
            checkFood(p.snake, foods, foodToRemove);
        }
        for (Bot b : bots) {
            if (b.snake.dead) continue;
            checkFood(b.snake, foods, foodToRemove);
        }

        // Build all snakes and owner maps
        List<Snake> allSnakes = new ArrayList<>();
        Map<Snake, Player> ownerPlayer = new IdentityHashMap<>();
        Map<Snake, Bot> ownerBot = new IdentityHashMap<>();

        for (Player p : players) {
            if (!p.snake.dead) {
                allSnakes.add(p.snake);
                ownerPlayer.put(p.snake, p);
            }
        }
        for (Bot b : bots) {
            if (!b.snake.dead) {
                allSnakes.add(b.snake);
                ownerBot.put(b.snake, b);
            }
        }

        // Determine deaths
        Set<Snake> deadSnakes = new HashSet<>();

        // 1) Head vs Body collisions (line segment-based)
        for (Snake headSnake : allSnakes) {
            if (deadSnakes.contains(headSnake)) continue;
            if (isHeadCollidingWithAnyBody(headSnake, allSnakes)) {
                deadSnakes.add(headSnake);
            }
        }

        // 2) Head-to-head collisions: smaller radius loses; equal => both
        int n = allSnakes.size();
        for (int i = 0; i < n; i++) {
            Snake a = allSnakes.get(i);
            if (deadSnakes.contains(a)) continue;

            for (int j = i + 1; j < n; j++) {
                Snake b = allSnakes.get(j);
                if (deadSnakes.contains(b)) continue;

                float dx = a.x - b.x;
                float dy = a.y - b.y;
                float distSq = dx * dx + dy * dy;
                float threshold = (a.radius + b.radius) * HEAD_TO_HEAD_FACTOR;
                if (distSq <= threshold * threshold) {
                    // Decide outcome based on radius (proxy for mass/size)
                    if (Math.abs(a.radius - b.radius) < 0.5f) {
                        deadSnakes.add(a);
                        deadSnakes.add(b);
                    } else if (a.radius < b.radius) {
                        deadSnakes.add(a);
                    } else {
                        deadSnakes.add(b);
                    }
                }
            }
        }

        // Apply food removals
        foods.removeAll(foodToRemove);

        // Apply deaths, drop food, and remove dead bots
        for (Snake s : deadSnakes) {
            Player p = ownerPlayer.get(s);
            Bot b = ownerBot.get(s);
            s.dead = true;

            dropFood(s, foods);

            if (p != null) {
                deadPlayers.add(p);
            } else if (b != null) {
                deadBots.add(b);
            }
        }

        // Remove dead bots from list
        bots.removeAll(deadBots);
        // Players remain in map; server/session code can respawn or clean up later
    }

    private static void checkFood(Snake s, List<Food> foods, Set<Food> toRemove) {
        float mouthReach = s.radius * 2.2f; // slight tweak for a smoother feel
        float mouthReachSq = mouthReach * mouthReach;

        for (Food f : foods) {
            float dx = s.x - f.x;
            float dy = s.y - f.y;
            if (dx * dx + dy * dy <= mouthReachSq) {
                s.score++;
                // Growth curve similar feel to Slither: diminishing returns as you get larger
                float growthFactor = 0.12f;
                if (s.radius > 30) growthFactor = 0.06f;
                if (s.radius > 50) growthFactor = 0.03f;
                if (s.radius > 80) growthFactor = 0.015f;
                if (s.radius < 100) s.radius += growthFactor;
                toRemove.add(f);
            }
        }
    }

    // Head vs any body (using segment distance for smoother collision)
    private static boolean isHeadCollidingWithAnyBody(Snake headSnake, List<Snake> allSnakes) {
        for (Snake otherSnake : allSnakes) {
            if (otherSnake.segments.size() < 2) continue;

            int startIndex = (headSnake == otherSnake) ? SELF_COLLISION_SKIP_SEGMENTS : 0;
            int last = otherSnake.segments.size() - 1;

            // Clamp startIndex to allow at least one segment
            if (startIndex >= last) startIndex = Math.max(0, last - 1);

            float effectiveRadius = headSnake.radius + otherSnake.radius * BODY_THICKNESS_FACTOR;
            float effectiveRadiusSq = effectiveRadius * effectiveRadius;

            for (int i = startIndex; i < last; i++) {
                Snake.Point a = otherSnake.segments.get(i);
                Snake.Point b = otherSnake.segments.get(i + 1);

                float dSq = Geometry.pointToSegmentDistanceSq(headSnake.x, headSnake.y, a.x, a.y, b.x, b.y);
                if (dSq <= effectiveRadiusSq) {
                    return true;
                }
            }
        }
        return false;
    }

    private static void dropFood(Snake s, List<Food> foods) {
        // Drop proportional to score, capped, with a spread around the death location
        int count = Math.min(Math.max(s.score / 2, 10), 150);
        for (int i = 0; i < count; i++) {
            float angle = (float) (Math.random() * Math.PI * 2);
            float radius = (float) (Math.random() * 75);
            float fx = s.x + (float) Math.cos(angle) * radius;
            float fy = s.y + (float) Math.sin(angle) * radius;
            foods.add(new Food(fx, fy, 1));
        }
    }
}
