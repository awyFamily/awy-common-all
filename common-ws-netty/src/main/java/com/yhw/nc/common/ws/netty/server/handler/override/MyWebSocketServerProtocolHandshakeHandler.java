package com.yhw.nc.common.ws.netty.server.handler.override;

import com.yhw.nc.common.ws.netty.context.GlobalContent;
import com.yhw.nc.common.ws.netty.model.ImSession;
import com.yhw.nc.common.ws.netty.process.AuthProcess;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.handler.ssl.SslHandler;

import java.util.HashMap;
import java.util.Map;

import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpResponseStatus.FORBIDDEN;
import static io.netty.handler.codec.http.HttpUtil.isKeepAlive;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * Handles the HTTP handshake (the HTTP Upgrade request) for {@link MyWebSocketServerProtocolHandler}.
 * @author yhw
 */
public class MyWebSocketServerProtocolHandshakeHandler extends ChannelInboundHandlerAdapter {

    //webSocket bind path
    private final String websocketPath;

    private final String subProtocols;
    private final boolean allowExtensions;
    private final int maxFramePayloadSize;
    private final boolean allowMaskMismatch;
    private final boolean checkStartsWith;

//    private AuthProcess authProcess;

    MyWebSocketServerProtocolHandshakeHandler(String websocketPath, String subProtocols,
                                              boolean allowExtensions, int maxFrameSize, boolean allowMaskMismatch, boolean checkStartsWith) {
        System.err.println("MyWebSocketServerProtocolHandshakeHandler init");
        this.websocketPath = websocketPath;
        this.subProtocols = subProtocols;
        this.allowExtensions = allowExtensions;
        maxFramePayloadSize = maxFrameSize;
        this.allowMaskMismatch = allowMaskMismatch;
        this.checkStartsWith = checkStartsWith;
    }


/*    MyWebSocketServerProtocolHandshakeHandler(String websocketPath, String subProtocols,
                                              boolean allowExtensions, int maxFrameSize, boolean allowMaskMismatch, boolean checkStartsWith, AuthProcess authProcess) {
        System.err.println("MyWebSocketServerProtocolHandshakeHandler init");
        this.websocketPath = websocketPath;
        this.subProtocols = subProtocols;
        this.allowExtensions = allowExtensions;
        maxFramePayloadSize = maxFrameSize;
        this.allowMaskMismatch = allowMaskMismatch;
        this.checkStartsWith = checkStartsWith;

        this.authProcess = authProcess;
    }*/

    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg) throws Exception {
        final FullHttpRequest req = (FullHttpRequest) msg;

        //校验请求 url
        if (isNotWebSocketPath(req)) {
            ctx.fireChannelRead(msg);
            return;
        }

        try {
            if (!GET.equals(req.method())) {
                sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HTTP_1_1, FORBIDDEN));
                return;
            }

            final WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(
                    getWebSocketLocation(ctx.pipeline(), req, websocketPath), subProtocols,
                    allowExtensions, maxFramePayloadSize, allowMaskMismatch);
            final WebSocketServerHandshaker handShaker = wsFactory.newHandshaker(req);
            if (handShaker == null) {
                WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
            } else {
                //握手回调
                final ChannelFuture handshakeFuture = handShaker.handshake(ctx.channel(), req);
                handshakeFuture.addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        if (!future.isSuccess()) {
                            ctx.fireExceptionCaught(future.cause());
                        } else {
                            // Kept for compatibility
                            ctx.fireUserEventTriggered(
                                    MyWebSocketServerProtocolHandler.ServerHandshakeStateEvent.HANDSHAKE_COMPLETE);

                            ctx.fireUserEventTriggered(
                                    new MyWebSocketServerProtocolHandler.HandshakeComplete(
                                            req.uri(), req.headers(), handShaker.selectedSubprotocol()));

                            //处理业务逻辑
                            processHandshake(req,ctx);

                        }
                    }
                });

                MyWebSocketServerProtocolHandler.setHandshaker(ctx.channel(), handShaker);
                //用当前处理器替换为 WS403Responder(禁止http请求)
                ctx.pipeline().replace(this, "WS403Responder",
                        MyWebSocketServerProtocolHandler.forbiddenHttpRequestResponder());
            }
        } finally {
            req.release();
        }
    }


    private boolean isNotWebSocketPath(FullHttpRequest req) {
//        return false;
        return checkStartsWith ? !req.uri().startsWith(websocketPath) : !req.uri().equals(websocketPath);
    }

    private static void sendHttpResponse(ChannelHandlerContext ctx, HttpRequest req, HttpResponse res) {
        ChannelFuture f = ctx.channel().writeAndFlush(res);
        if (!isKeepAlive(req) || res.status().code() != 200) {
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }

    private static String getWebSocketLocation(ChannelPipeline cp, HttpRequest req, String path) {
        String protocol = "ws";
        if (cp.get(SslHandler.class) != null) {
            // SSL in use so use Secure WebSockets
            protocol = "wss";
        }
        String host = req.headers().get(HttpHeaderNames.HOST);
        return protocol + "://" + host + path;
    }


    /**
     * 执行握手操作
     * @param req 请求
     * @param ctx 通道上下文
     */
    private void processHandshake(HttpRequest req, ChannelHandlerContext ctx){
        HttpMethod method = req.method();
        Map<String,String> parameterMap = new HashMap<>();
        if (HttpMethod.GET == method) {
            // 是GET请求
            QueryStringDecoder decoder = new QueryStringDecoder(req.uri());
            decoder.parameters().entrySet().forEach( entry -> {
                // entry.getValue()是一个List, 只取第一个元素
                parameterMap.put(entry.getKey(), entry.getValue().get(0));
            });
        }


//        ImSession login = authProcess.login(parameterMap.get("userName"), parameterMap.get("passwod"));
        ImSession login = GlobalContent.getInstance().getAuthProcess().login(parameterMap.get("userName"), parameterMap.get("passwod"));

        if(login == null){
            System.err.println("认证失败，关闭连接");
            ctx.channel().close();
            return;
        }
        //认证成功，绑定上下文信息
        GlobalContent.getInstance().getLifeCycleEvent().bindContext(login,ctx);

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        //浏览器直接关闭，此处不会被触发
        System.err.println(this.getClass().getName() + " channel 被关闭");
    }

}
