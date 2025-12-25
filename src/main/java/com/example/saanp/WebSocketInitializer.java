package com.example.saanp;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;

public class WebSocketInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) {
        ChannelPipeline p = ch.pipeline();
        p.addLast(new HttpServerCodec());
        p.addLast(new HttpObjectAggregator(65536));
        
        // 1. Handle HTTP Health Checks first
        p.addLast(new HealthCheckHandler());
        
        // 2. Handle WebSocket handshake for /play
        p.addLast(new WebSocketServerProtocolHandler("/play", null, true));
        
        // 3. Handle Game Logic
        p.addLast(new WebSocketHandler());
    }
}
