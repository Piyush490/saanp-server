package com.example.saanp;

import static com.example.saanp.GameLoop.TICK_MS;

public class Snake {

    public float x = (float) (Math.random() * GameRoom.MAP_SIZE);
    public float y = (float) (Math.random() * GameRoom.MAP_SIZE);
    public double angle = 0;

    public float speed = 140f;   // units per second
    public float radius = 18f;
    public boolean dead = false;

    public void update(double targetAngle, boolean boost) {

        // ---- TURNING ----
        double diff = targetAngle - angle;

        // Normalize to [-PI, PI]
        diff = Math.atan2(Math.sin(diff), Math.cos(diff));

        // Turn speed in radians per second
        double turnSpeed = 3.5; // Slither-like smoothness

        float delta = TICK_MS / 1000f;

        double maxTurnThisTick = turnSpeed * delta;
        diff = Math.max(-maxTurnThisTick, Math.min(maxTurnThisTick, diff));
        angle += diff;

        // ---- SPEED ----
        float baseSpeed = 140f;
        float boostSpeed = 240f;
        float speed = boost ? boostSpeed : baseSpeed;

        x += Math.cos(angle) * speed * delta;
        y += Math.sin(angle) * speed * delta;

        // ---- BOUNDARY CHECK ----
        if (x < 0) x = 0;
        if (x > GameRoom.MAP_SIZE) x = GameRoom.MAP_SIZE;
        if (y < 0) y = 0;
        if (y > GameRoom.MAP_SIZE) y = GameRoom.MAP_SIZE;
    }

}
