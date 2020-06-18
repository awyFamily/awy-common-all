package com.awy.common.ws.client.handler;

import com.awy.common.ws.client.reader.CloseReader;
import com.awy.common.ws.client.reader.SimpleCloseReader;
import com.awy.common.ws.client.reader.SimpleTextReader;
import com.awy.common.ws.client.reader.TextReader;
import io.netty.channel.*;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yhw
 */
@Slf4j
public class WebSocketClientHandler extends SimpleChannelInboundHandler<Object> {

    private final WebSocketClientHandshaker handshaker;
    private ChannelPromise handshakeFuture;

    private  TextReader textReader;

    private  CloseReader closeReader;

    public WebSocketClientHandler(WebSocketClientHandshaker handshaker) {
        this(handshaker,new SimpleTextReader(),new SimpleCloseReader());
    }

    public WebSocketClientHandler(WebSocketClientHandshaker handshaker,TextReader textReader,CloseReader closeReader) {
        this.handshaker = handshaker;
        this.textReader = textReader == null ? new SimpleTextReader() : textReader;
        this.closeReader = closeReader == null ? new SimpleCloseReader() : closeReader;
    }

    public ChannelFuture handshakeFuture() {
        return handshakeFuture;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        handshakeFuture = ctx.newPromise();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        handshaker.handshake(ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        System.out.println("WebSocket Client disconnected!");
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        Channel ch = ctx.channel();
        if (!handshaker.isHandshakeComplete()) {
            try {
                handshaker.finishHandshake(ch, (FullHttpResponse) msg);
                log.info("WebSocket Client connected!");
                handshakeFuture.setSuccess();
            } catch (WebSocketHandshakeException e) {
                log.error("WebSocket Client failed to connect");
                closeReader.onClose();
                handshakeFuture.setFailure(e);
            }
            return;
        }

        if (msg instanceof FullHttpResponse) {
            FullHttpResponse response = (FullHttpResponse) msg;
            throw new IllegalStateException(
                    "Unexpected FullHttpResponse (getStatus=" + response.status() +
                            ", content=" + response.content().toString(CharsetUtil.UTF_8) + ')');
        }

        WebSocketFrame frame = (WebSocketFrame) msg;
        if (frame instanceof TextWebSocketFrame) {
            TextWebSocketFrame textFrame = (TextWebSocketFrame) frame;
            textReader.handler(textFrame.text(),ctx.channel());
        } else if (frame instanceof PongWebSocketFrame) {
            log.info("pong ....");
        } else if (frame instanceof CloseWebSocketFrame) {
            ch.close();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        if (!handshakeFuture.isDone()) {
            handshakeFuture.setFailure(cause);
        }
        ctx.close();
    }
}
