package com.example.saanp;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class GameRoom {

    private static final int MAX_FOOD = 300;
    private static final float MAP_SIZE = 5000f;

    private final List<Player> players = new CopyOnWriteArrayList<>();
    private final List<Food> foods = new CopyOnWriteArrayList<>();
    private final GameLoop loop = new GameLoop(this);

    public GameRoom() {
        spawnInitialFood();
        loop.start();
    }

    private void spawnInitialFood() {
        while (foods.size() < MAX_FOOD) {
            foods.add(randomFood());
        }
    }

    private Food randomFood() {
        return new Food(
                (float) (Math.random() * MAP_SIZE),
                (float) (Math.random() * MAP_SIZE),
                1
        );
    }

    public void addPlayer(Player p) {
        players.add(p);
    }

    public void removePlayer(Player p) {
        players.remove(p);
    }

    public List<Player> getPlayers() {
        return players;
    }

    public List<Food> getFoods() {
        return foods;
    }

    public void update() {
        for (Player p : players) {
            p.snake.update(p.inputAngle, p.boosting);
        }

        CollisionSystem.resolve(players, foods);

        while (foods.size() < MAX_FOOD) {
            foods.add(randomFood());
        }
    }
}
