package com.example.saanp;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;

public class HealthCheckHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req) {
        // Simple health check for Render/Cloud deployment
        if ("/".equals(req.uri())) {
            FullHttpResponse res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
            ctx.writeAndFlush(res).addListener(ChannelFutureListener.CLOSE);
        } else {
            // Pass the request to the next handler (WebSocket)
            ctx.fireChannelRead(req.retain());
        }
    }
}
