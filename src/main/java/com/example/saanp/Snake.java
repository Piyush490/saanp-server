package com.example.saanp;

import static com.example.saanp.GameLoop.TICK_MS;

public class Snake {

    public float x;
    public float y;
    public double angle = 0;

    public float radius = 15f;
    public int score = 0;
    public boolean dead = false;

    public Snake() {
        // Spawn inside circular map
        double spawnAngle = Math.random() * Math.PI * 2;
        double spawnRadius = Math.sqrt(Math.random()) * (GameRoom.MAP_RADIUS * 0.8f); // 80% of radius to avoid edges
        this.x = (float) (GameRoom.MAP_RADIUS + Math.cos(spawnAngle) * spawnRadius);
        this.y = (float) (GameRoom.MAP_RADIUS + Math.sin(spawnAngle) * spawnRadius);
        this.angle = spawnAngle; // Start facing outwards
    }

    public void update(double targetAngle, boolean boost) {
        if (dead) return;

        float delta = TICK_MS / 1000f;

        // ---- TURNING ----
        double diff = targetAngle - angle;
        // Normalize to [-PI, PI]
        diff = Math.atan2(Math.sin(diff), Math.cos(diff));

        // Turn speed in radians per second.
        // Smooth constant turning
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

        // Always move forward at currentSpeed
        x += Math.cos(angle) * currentSpeed * delta;
        y += Math.sin(angle) * currentSpeed * delta;

        // ---- BOUNDARY CHECK (Circular) ----
        float dx = x - GameRoom.MAP_RADIUS;
        float dy = y - GameRoom.MAP_RADIUS;
        float dist = (float) Math.sqrt(dx * dx + dy * dy);

        // Die if the head (edge of circle) touches the boundary
        if (dist + radius > GameRoom.MAP_RADIUS) {
            dead = true;
        }
    }

}
