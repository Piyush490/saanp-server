package com.example.saanp;

import io.netty.channel.Channel;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class GameRoom {

    private static final GameRoom INSTANCE = new GameRoom();
    public static GameRoom getInstance() { return INSTANCE; }

    private static final int MAX_FOOD = 300;
    private static final int MAX_BOTS = 10;
    public static final float MAP_SIZE = 10000f;
    public static final float MAP_RADIUS = 5000f;

    private static final int[] BOT_COLORS = {
            0xFFFF0000, 0xFF00FF00, 0xFF0000FF, 0xFFFFFF00, 0xFFFF00FF,
            0xFF00FFFF, 0xFFFFA500, 0xFF800080, 0xFFFFC0CB, 0xFFFFFFFF
    };

    private final Map<String, Player> players = new ConcurrentHashMap<>();
    private final List<Bot> bots = new CopyOnWriteArrayList<>();
    private final List<Food> foods = new CopyOnWriteArrayList<>();
    private final GameLoop loop; // initialized after world is ready

    private GameRoom() {
        // Build world first (no threads started yet)
        spawnInitialFood();
        spawnInitialBots();

        // If you have a GameLoop with a tick duration, set Snake.TICK_SECONDS accordingly:
        // Snake.TICK_SECONDS = GameLoop.TICK_MS / 1000f; // ensure GameLoop.TICK_MS is a compile-time constant or harmless

        // Now create and start the loop
        loop = new GameLoop(this);
        loop.start();
    }

    private void spawnInitialFood() {
        while (foods.size() < MAX_FOOD) {
            foods.add(randomFood());
        }
    }

    private void spawnInitialBots() {
        String[] names = {"SlitherBot", "SaanpAI", "DroidSnake", "Nibbler", "PythonBot", "Viper", "CobraBot", "Mamba", "Anaconda", "Boa"};
        for (int i = 0; i < MAX_BOTS; i++) {
            String name = names[i % names.length];
            int color = BOT_COLORS[i % BOT_COLORS.length];
            bots.add(new Bot(name, color));
        }
    }

    private Food randomFood() {
        double angle = Math.random() * Math.PI * 2;
        double radius = Math.sqrt(Math.random()) * MAP_RADIUS;
        float x = (float) (MAP_RADIUS + Math.cos(angle) * radius);
        float y = (float) (MAP_RADIUS + Math.sin(angle) * radius);
        return new Food(x, y, 1);
    }

    public void addPlayer(Player p) {
        String id = p.channel.id().asShortText();
        if (players.containsKey(id)) return;
        players.put(id, p);
    }

    public void removePlayerByChannel(Channel channel) {
        String id = channel.id().asShortText();
        players.remove(id);
    }

    public Collection<Player> getPlayers() { return players.values(); }
    public List<Bot> getBots() { return bots; }
    public List<Food> getFoods() { return foods; }

    public void update() {
        // Update players
        for (Player p : players.values()) {
            p.snake.update(p.inputAngle, p.boosting);
        }

        // Update bots
        List<Player> playerList = new ArrayList<>(players.values());
        for (Bot b : bots) {
            b.update(foods, playerList, bots);
        }

        // Resolve collisions
        CollisionSystem.resolve(players.values(), bots, foods);

        // Refill food and bots
        while (foods.size() < MAX_FOOD) {
            foods.add(randomFood());
        }
        if (bots.size() < MAX_BOTS) {
            String[] names = {"BotBuddy", "Snakey", "AI_Player", "Crawler", "Hunter", "Stalker"};
            int randomColor = BOT_COLORS[(int)(Math.random() * BOT_COLORS.length)];
            bots.add(new Bot(names[(int)(Math.random() * names.length)], randomColor));
        }
    }

    public void removeInactivePlayers(long now) {
        players.values().removeIf(p -> !p.channel.isActive() && now - p.lastSeen > 10_000);
    }
}
