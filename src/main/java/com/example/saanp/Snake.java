package com.example.saanp;

import java.util.LinkedList;
import java.util.List;
import static com.example.saanp.GameLoop.TICK_MS;

public class Snake {

    public float x;
    public float y;
    public double angle = 0;

    public float radius = 15f;
    public int score = 0;
    public boolean dead = false;

    // Body segments for collision detection
    public final List<Point> segments = new LinkedList<>();
    private static final int MAX_SEGMENTS = 200;

    public Snake() {
        // Spawn inside circular map
        double spawnAngle = Math.random() * Math.PI * 2;
        double spawnRadius = Math.sqrt(Math.random()) * (GameRoom.MAP_RADIUS * 0.8f); 
        this.x = (float) (GameRoom.MAP_RADIUS + Math.cos(spawnAngle) * spawnRadius);
        this.y = (float) (GameRoom.MAP_RADIUS + Math.sin(spawnAngle) * spawnRadius);
        this.angle = spawnAngle;

        // Initialize with a few segments at the spawn position
        for (int i = 0; i < 5; i++) {
            segments.add(new Point(x, y));
        }
    }

    public void update(double targetAngle, boolean boost) {
        if (dead) return;

        float delta = TICK_MS / 1000f;

        // ---- TURNING ----
        double diff = targetAngle - angle;
        diff = Math.atan2(Math.sin(diff), Math.cos(diff));
        double turnSpeed = 3.8; 
        double maxTurnThisTick = turnSpeed * delta;
        
        if (Math.abs(diff) > maxTurnThisTick) {
            angle += (diff > 0 ? 1 : -1) * maxTurnThisTick;
        } else {
            angle = targetAngle;
        }

        // ---- SPEED ----
        float baseSpeed = 180f;
        float boostSpeed = 320f;
        float currentSpeed = boost ? boostSpeed : baseSpeed;

        x += Math.cos(angle) * currentSpeed * delta;
        y += Math.sin(angle) * currentSpeed * delta;

        // ---- BODY UPDATE ----
        segments.add(0, new Point(x, y));
        
        int targetLen = 10 + (score / 2);
        if (targetLen > MAX_SEGMENTS) targetLen = MAX_SEGMENTS;
        
        while (segments.size() > targetLen) {
            segments.remove(segments.size() - 1);
        }

        // ---- BOUNDARY CHECK ----
        float dx = x - GameRoom.MAP_RADIUS;
        float dy = y - GameRoom.MAP_RADIUS;
        float dist = (float) Math.sqrt(dx * dx + dy * dy);

        if (dist + radius > GameRoom.MAP_RADIUS) {
            dead = true;
        }
    }

    // Static helper class for coordinates
    public static class Point {
        public float x, y;
        public Point(float x, float y) { this.x = x; this.y = y; }
    }
}
