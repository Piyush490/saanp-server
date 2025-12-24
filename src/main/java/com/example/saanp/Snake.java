package com.example.saanp;

import static com.example.saanp.GameLoop.TICK_MS;

public class Snake {

    public float x = (float) (Math.random() * 4000);
    public float y = (float) (Math.random() * 4000);
    public double angle = 0;

    public float speed = 140f;   // units per second
    public float radius = 18f;
    public boolean dead = false;

    public void update(double targetAngle, boolean boost) {

        double maxTurn = 0.15;
        double diff = targetAngle - angle;

        // normalize angle difference (-PI to PI)
        diff = Math.atan2(Math.sin(diff), Math.cos(diff));
        angle += Math.max(-maxTurn, Math.min(maxTurn, diff));

        float baseSpeed = 140f;
        float boostSpeed = 240f;
        speed = boost ? boostSpeed : baseSpeed;

        float delta = TICK_MS / 1000f;
        x += Math.cos(angle) * speed * delta;
        y += Math.sin(angle) * speed * delta;
    }
}

