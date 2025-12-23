package com.example.saanp;

import java.util.Iterator;
import java.util.List;

public class CollisionSystem {

    public static void resolve(List<Player> players, List<Food> foods) {

        // Snake eats food
        for (Player p : players) {
            Iterator<Food> it = foods.iterator();
            while (it.hasNext()) {
                Food f = it.next();
                float dx = p.snake.x - f.x;
                float dy = p.snake.y - f.y;
                if (dx * dx + dy * dy < p.snake.radius * p.snake.radius) {
                    p.snake.radius += f.value;
                    it.remove();
                }
            }
        }

        // Snake vs snake
        for (Player a : players) {
            for (Player b : players) {
                if (a == b) continue;

                float dx = a.snake.x - b.snake.x;
                float dy = a.snake.y - b.snake.y;
                float distSq = dx * dx + dy * dy;

                if (distSq < b.snake.radius * b.snake.radius * 0.8f) {
                    a.snake.dead = true;
                }
            }
        }

        // Handle deaths
        Iterator<Player> pit = players.iterator();
        while (pit.hasNext()) {
            Player p = pit.next();
            if (p.snake.dead) {
                dropFood(p, foods);
                pit.remove();
                p.channel.close();
            }
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
