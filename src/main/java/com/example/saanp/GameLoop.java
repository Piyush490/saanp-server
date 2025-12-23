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
        room.update();
        Protocol.broadcast(room);
        long now = System.currentTimeMillis();
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
