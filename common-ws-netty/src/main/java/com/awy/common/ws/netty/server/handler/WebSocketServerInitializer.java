package com.awy.common.ws.netty.server.handler;

import com.awy.common.ws.netty.server.handler.override.MyWebSocketServerProtocolHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * 初始化
 * @author yhw
 */
@ChannelHandler.Sharable
public class WebSocketServerInitializer extends ChannelInitializer<SocketChannel> {


    private SslContext sslCtx;
    String websocketPath;
    String subProtocols;
    boolean allowExtensions;
    boolean checkStartsWith;


    private WebSocketServerInitializer(){}

    public WebSocketServerInitializer(SslContext sslCtx, String websocketPath){
        this(sslCtx,websocketPath,null,true,true);
    }


    public WebSocketServerInitializer(SslContext sslCtx, String websocketPath, String subProtocols, boolean allowExtensions, boolean checkStartsWith){
        this.sslCtx = sslCtx;
        this.websocketPath = websocketPath;
        //子协议
        this.subProtocols = subProtocols;
        //需要 设置为true(运行拓展)
        this.allowExtensions = allowExtensions;
        //严格校验webSocket请求路径(false , true 无法进行认证)
        this.checkStartsWith = checkStartsWith;
    }

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        if (sslCtx != null) {
            pipeline.addLast(sslCtx.newHandler(ch.alloc()));
        }
        //http 编码解码器
        pipeline.addLast(new HttpServerCodec());
        //把多个消息转换为一个单一的FullHttpRequest或是FullHttpResponse，原因是HTTP解码器会在每个HTTP消息中生成多个消息对象
        pipeline.addLast(new HttpObjectAggregator(65536));
        //WebSocket数据压缩
        pipeline.addLast(new WebSocketServerCompressionHandler());
        //webSocket 协议处理
        pipeline.addLast(new MyWebSocketServerProtocolHandler(websocketPath,subProtocols,allowExtensions,checkStartsWith));
        //http 首页处理器
        pipeline.addLast(new WebSocketIndexPageHandler(websocketPath));
        //webSocket处理器
        pipeline.addLast(new WebSocketFrameHandler());
        //空闲检测
        pipeline.addLast(new IdleStateHandler(0, 0, 360));
    }
}
