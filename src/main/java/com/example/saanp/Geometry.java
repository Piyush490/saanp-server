package com.example.saanp;

public final class Geometry {
    private Geometry() {}

    public static float distSq(float x1, float y1, float x2, float y2) {
        float dx = x1 - x2;
        float dy = y1 - y2;
        return dx * dx + dy * dy;
    }

    public static float pointToSegmentDistanceSq(float px, float py, float x1, float y1, float x2, float y2) {
        float vx = x2 - x1;
        float vy = y2 - y1;

        float wx = px - x1;
        float wy = py - y1;

        float c1 = vx * wx + vy * wy;
        if (c1 <= 0f) {
            return distSq(px, py, x1, y1);
        }

        float c2 = vx * vx + vy * vy;
        if (c2 <= c1) {
            return distSq(px, py, x2, y2);
        }

        float t = c1 / c2; // projection factor in [0,1]
        float cx = x1 + t * vx;
        float cy = y1 + t * vy;
        return distSq(px, py, cx, cy);
    }
}
