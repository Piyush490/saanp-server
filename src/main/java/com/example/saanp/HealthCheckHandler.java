package com.example.saanp;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class HealthCheckHandler
        extends SimpleChannelInboundHandler<FullHttpRequest> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx,
                                FullHttpRequest req) {

        if (req.uri().equals("/")) {

            byte[] body = "OK".getBytes();

            FullHttpResponse response =
                    new DefaultFullHttpResponse(
                            HTTP_1_1,
                            OK,
                            Unpooled.wrappedBuffer(body)
                    );

            response.headers().set(CONTENT_TYPE, "text/plain");
            response.headers().set(CONTENT_LENGTH, body.length);

            ctx.writeAndFlush(response);
            return;
        }

        // Forward WebSocket upgrade requests
        ctx.fireChannelRead(req.retain());
    }
}
