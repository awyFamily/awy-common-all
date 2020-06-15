package com.yhw.nc.common.ws.netty.server.handler.override;

import com.yhw.nc.common.ws.netty.constants.ImCommonConstant;
import com.yhw.nc.common.ws.netty.context.GlobalContent;
import com.yhw.nc.common.ws.netty.process.AuthProcess;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.Utf8FrameValidator;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.util.AttributeKey;

import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * web socket 协议处理（进行握手）
 * @author yhw
 */
@ChannelHandler.Sharable
public class MyWebSocketServerProtocolHandler extends WebSocketServerProtocolHandler {

    private  String websocketPath;
    private  String subProtocols;
    private  boolean allowExtensions;
    private  int maxFramePayloadLength = 65536;

    /**
     * 是否按照前缀检查 url请求(false 则需要和 websocketPath完全匹配)
     */
    private  boolean checkStartsWith;

//    private AuthProcess authProcess;


    public MyWebSocketServerProtocolHandler(String websocketPath, String subProtocols, boolean allowExtensions, boolean checkStartsWith) {
        super(websocketPath,subProtocols,allowExtensions);
        this.websocketPath = websocketPath;
        this.subProtocols = subProtocols;
        this.allowExtensions = allowExtensions;
        this.checkStartsWith = checkStartsWith;

//        this.authProcess = authProcess;
    }

    private static final AttributeKey<WebSocketServerHandshaker> MY_HANDSHAKER_ATTR_KEY =
            AttributeKey.valueOf(WebSocketServerHandshaker.class, ImCommonConstant.HANDSHAKER);

    static void setHandshaker(Channel channel, WebSocketServerHandshaker handshaker) {
        channel.attr(MY_HANDSHAKER_ATTR_KEY).set(handshaker);
    }

    static ChannelHandler forbiddenHttpRequestResponder() {
        return new ChannelInboundHandlerAdapter() {
            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                if (msg instanceof FullHttpRequest) {
                    ((FullHttpRequest) msg).release();
                    FullHttpResponse response =
                            new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.FORBIDDEN);
                    ctx.channel().writeAndFlush(response);
                } else {
                    ctx.fireChannelRead(msg);
                }
            }
        };
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        System.err.println("============== webSocket 握手handler ,只需进行一次握手======================");
//        super.handlerAdded(ctx);
        ChannelPipeline cp = ctx.pipeline();
        if (cp.get(MyWebSocketServerProtocolHandshakeHandler.class) == null) {
            // Add the WebSocketHandshakeHandler before this one.
            ctx.pipeline().addBefore(ctx.name(), MyWebSocketServerProtocolHandshakeHandler.class.getName(),
                    new MyWebSocketServerProtocolHandshakeHandler(websocketPath, subProtocols,
                            allowExtensions, maxFramePayloadLength, false, checkStartsWith));
        }
        if (cp.get(Utf8FrameValidator.class) == null) {
            // Add the UFT8 checking before this one.
            ctx.pipeline().addBefore(ctx.name(), Utf8FrameValidator.class.getName(),
                    new Utf8FrameValidator());
        }
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        //第一步 通道关闭触发(解绑操作)
        System.err.println(this.getClass().getName() + " channel 被关闭：channelInactive() 进行解绑操作！");
        GlobalContent.getInstance().getLifeCycleEvent().cleanContext(ctx.channel());
//        super.channelInactive(ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        //第二步
        System.err.println(this.getClass().getName() +" channel 取消线程(NioEventLoop) 的绑定: channelUnregistered()");
//        super.channelUnregistered(ctx);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        //第三步骤
        System.err.println(this.getClass().getName() + " channel 逻辑处理器被移除：handlerRemoved()");
//        super.handlerRemoved(ctx);
    }


    public static final class HandshakeComplete {
        private final String requestUri;
        private final HttpHeaders requestHeaders;
        private final String selectedSubProtocol;

        HandshakeComplete(String requestUri, HttpHeaders requestHeaders, String selectedSubProtocol) {
            this.requestUri = requestUri;
            this.requestHeaders = requestHeaders;
            this.selectedSubProtocol = selectedSubProtocol;
        }

        public String requestUri() {
            return requestUri;
        }

        public HttpHeaders requestHeaders() {
            return requestHeaders;
        }

        public String selectedSubProtocol() {
            return selectedSubProtocol;
        }
    }
}