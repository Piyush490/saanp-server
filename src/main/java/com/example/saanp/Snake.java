package com.example.saanp;

public class Snake {

    public float x = (float) (Math.random() * 4000);
    public float y = (float) (Math.random() * 4000);
    public double angle = 0;
    public float speed = 3f;
    public float radius = 18f;
    public boolean dead = false;

    public void update(double targetAngle, boolean boost) {
        double maxTurn = 0.15;
        angle += Math.max(-maxTurn, Math.min(maxTurn, targetAngle - angle));
        speed = boost ? 6f : 3f;

        x += Math.cos(angle) * speed;
        y += Math.sin(angle) * speed;
    }
}
