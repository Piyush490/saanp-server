package com.example.saanp;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GameLoop {

    private static final int TICK_MS = 50; // 20 TPS
    private final GameRoom room;
    private final ScheduledExecutorService executor =
            Executors.newSingleThreadScheduledExecutor();

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
        room.update();
        Protocol.broadcast(room);
    }

    public void stop() {
        executor.shutdownNow();
    }
}
