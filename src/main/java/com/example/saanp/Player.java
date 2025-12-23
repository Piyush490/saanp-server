package com.example.saanp;

import io.netty.channel.Channel;

public class Player {

    public final Channel channel;
    public final Snake snake = new Snake();

    public volatile double inputAngle = 0;
    public volatile boolean boosting = false;

    public Player(Channel channel) {
        this.channel = channel;
    }
}
