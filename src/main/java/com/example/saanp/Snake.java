package com.example.saanp;

import static com.example.saanp.GameLoop.TICK_MS;

public class Snake {

    public float x;
    public float y;
    public double angle = 0;

    public float speed = 140f;   // units per second
    public float radius = 18f;
    public boolean dead = false;

    public Snake() {
        // Spawn inside circular map
        double spawnAngle = Math.random() * Math.PI * 2;
        double spawnRadius = Math.sqrt(Math.random()) * (GameRoom.MAP_RADIUS * 0.8f); // 80% of radius to avoid edges
        this.x = (float) (GameRoom.MAP_RADIUS + Math.cos(spawnAngle) * spawnRadius);
        this.y = (float) (GameRoom.MAP_RADIUS + Math.sin(spawnAngle) * spawnRadius);
    }

    public void update(double targetAngle, boolean boost) {
        if (dead) return;

        // ---- TURNING ----
        double diff = targetAngle - angle;

        // Normalize to [-PI, PI]
        diff = Math.atan2(Math.sin(diff), Math.cos(diff));

        // Turn speed in radians per second
        // Real Slither turn speed is tighter when smaller and slower when larger/boosting
        double turnSpeed = 4.5; 

        float delta = TICK_MS / 1000f;

        double maxTurnThisTick = turnSpeed * delta;
        diff = Math.max(-maxTurnThisTick, Math.min(maxTurnThisTick, diff));
        angle += diff;

        // ---- SPEED ----
        float baseSpeed = 160f;
        float boostSpeed = 300f;
        float currentSpeed = boost ? boostSpeed : baseSpeed;

        x += Math.cos(angle) * currentSpeed * delta;
        y += Math.sin(angle) * currentSpeed * delta;

        // ---- BOUNDARY CHECK (Circular) ----
        float dx = x - GameRoom.MAP_RADIUS;
        float dy = y - GameRoom.MAP_RADIUS;
        float distSq = dx * dx + dy * dy;

        if (distSq > GameRoom.MAP_RADIUS * GameRoom.MAP_RADIUS) {
            dead = true;
        }
    }

}
