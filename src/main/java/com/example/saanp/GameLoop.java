package com.example.saanp;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GameLoop {

    private static final int TICK_MS = 50; // 20 TPS
    private final GameRoom room;
    private final ScheduledExecutorService executor =
            Executors.newSingleThreadScheduledExecutor();
    private long lastLogTime = 0;


    public GameLoop(GameRoom room) {
        this.room = room;
    }

    public void start() {
        executor.scheduleAtFixedRate(
                this::tick,
                0,
                TICK_MS,
                TimeUnit.MILLISECONDS
        );
    }

    private void tick() {

        long now = System.currentTimeMillis();

        // 1️⃣ Update world
        room.update();

        // 2️⃣ Remove stale players (ONLY HERE)
        room.removeInactivePlayers(now);

        // 3️⃣ Broadcast snapshot
        Protocol.broadcast(room);

        // 4️⃣ Log once per second
        if (now - lastLogTime > 1000) {
            System.out.println(
                    "[LOOP] tick players=" + room.getPlayers().size() +
                            " food=" + room.getFoods().size()
            );
            lastLogTime = now;
        }
    }


    public void stop() {
        executor.shutdownNow();
    }
}
