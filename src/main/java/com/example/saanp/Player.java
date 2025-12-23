package com.example.saanp;

import io.netty.channel.Channel;

public class Player {

    public final Channel channel;

    public final String name;
    public final int color;

    public final Snake snake;

    public float inputAngle;
    public boolean boosting;

    public Player(Channel channel, String name, int color) {
        this.channel = channel;
        this.name = name;
        this.color = color;
        this.snake = new Snake();
    }
}
