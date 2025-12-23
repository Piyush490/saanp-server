package com.example.saanp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CollisionSystem {

    public static void resolve(Collection<Player> players, List<Food> foods) {

        List<Food> foodToRemove = new ArrayList<>();
        List<Player> deadPlayers = new ArrayList<>();

        // 🟢 Snake eats food
        for (Player p : players) {
            if (p.snake.dead) continue;

            for (Food f : foods) {
                float dx = p.snake.x - f.x;
                float dy = p.snake.y - f.y;

                if (dx * dx + dy * dy < p.snake.radius * p.snake.radius) {
                    p.snake.radius += f.value;
                    foodToRemove.add(f);
                }
            }
        }

        // 🟢 Snake vs snake
        for (Player a : players) {
            if (a.snake.dead) continue;

            for (Player b : players) {
                if (a == b) continue;

                float dx = a.snake.x - b.snake.x;
                float dy = a.snake.y - b.snake.y;
                float distSq = dx * dx + dy * dy;

                if (distSq < b.snake.radius * b.snake.radius * 0.8f) {
                    a.snake.dead = true;
                    deadPlayers.add(a);
                    break;
                }
            }
        }

        // 🟢 Apply removals SAFELY
        foods.removeAll(foodToRemove);

        for (Player p : deadPlayers) {
            dropFood(p, foods);
            p.channel.close(); // channel closes → GameLoop cleanup removes player
        }
    }

    private static void dropFood(Player p, List<Food> foods) {
        for (int i = 0; i < p.snake.radius; i += 5) {
            foods.add(new Food(
                    p.snake.x + (float) (Math.random() * 30 - 15),
                    p.snake.y + (float) (Math.random() * 30 - 15),
                    1
            ));
        }
    }
}
