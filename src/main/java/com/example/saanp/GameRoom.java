package com.example.saanp;

import io.netty.channel.Channel;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class GameRoom {

    private static final GameRoom INSTANCE = new GameRoom();

    public static GameRoom getInstance() {
        return INSTANCE;
    }

    private static final int MAX_FOOD = 300;
    private static final float MAP_SIZE = 5000f;

    private final List<Player> players = new CopyOnWriteArrayList<>();
    private final List<Food> foods = new CopyOnWriteArrayList<>();
    private final GameLoop loop = new GameLoop(this);

    // 🔒 private constructor
    private GameRoom() {
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
        System.out.println(
                "[ROOM] Player added. totalPlayers=" + players.size()
        );
    }

    public void removePlayer(Player p) {
        players.remove(p);
    }

    public void removePlayerByChannel(Channel channel) {
        players.removeIf(p -> p.channel == channel);
        System.out.println(
                "[ROOM] Player removed. totalPlayers=" + players.size()
        );
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
