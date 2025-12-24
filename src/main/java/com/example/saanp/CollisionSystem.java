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

        // 🟢 Snake vs Snake (Player vs Player, Player vs Bot, Bot vs Bot)
        // Check Players
        for (Player p : players) {
            if (p.snake.dead) continue;
            
            // vs other players
            for (Player other : players) {
                if (p == other || other.snake.dead) continue;
                if (checkCollision(p.snake, other.snake)) {
                    p.snake.dead = true;
                    deadPlayers.add(p);
                    break;
                }
            }
            if (p.snake.dead) continue;

            // vs bots
            for (Bot b : bots) {
                if (b.snake.dead) continue;
                if (checkCollision(p.snake, b.snake)) {
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
                if (checkCollision(b.snake, p.snake)) {
                    b.snake.dead = true;
                    deadBots.add(b);
                    break;
                }
            }
            if (b.snake.dead) continue;

            // vs other bots
            for (Bot other : bots) {
                if (b == other || other.snake.dead) continue;
                if (checkCollision(b.snake, other.snake)) {
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
            if (dx * dx + dy * dy < s.radius * s.radius) {
                s.radius += 0.5f; // Slow growth
                toRemove.add(f);
            }
        }
    }

    private static boolean checkCollision(Snake head, Snake body) {
        float dx = head.x - body.x;
        float dy = head.y - body.y;
        float distSq = dx * dx + dy * dy;
        // If head is inside the body of another snake
        return distSq < body.radius * body.radius * 0.8f;
    }

    private static void dropFood(Snake s, List<Food> foods) {
        for (int i = 0; i < s.radius; i += 5) {
            foods.add(new Food(
                    s.x + (float) (Math.random() * 60 - 30),
                    s.y + (float) (Math.random() * 60 - 30),
                    1
            ));
        }
    }
}
