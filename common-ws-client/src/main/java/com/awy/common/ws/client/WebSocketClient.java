package com.awy.common.ws.client;

import cn.hutool.json.JSONUtil;
import com.awy.common.message.api.packets.Message;
import com.awy.common.ws.client.handler.WebSocketClientHandler;
import com.awy.common.ws.client.reader.CloseReader;
import com.awy.common.ws.client.reader.TextReader;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketClientCompressionHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.util.concurrent.TimeUnit;


/**
 * 客户端
 * @author yhw
 */
@Slf4j
public final class WebSocketClient {

    //    static final String URL = System.getProperty("url", "ws://127.0.0.1:8080/websocket");
    private URI uri;

    private String host;

    private int port;

    private SslContext sslCtx;

    private Channel channel;

    private EventLoopGroup group;

    private Bootstrap bootstrap;

    private  WebSocketClientHandler handler;

    private TextReader textReader;

    private CloseReader closeReader;

    //是否断线重连
    private boolean isRetry;

    private WebSocketClient(){
        this("ws://127.0.0.1:8080/websocket",null);
    }

    public WebSocketClient(String url,TextReader textReader) {
        this(url,textReader,null,true);
    }


    /**
     * @param url 监听url
     * @param textReader 收到文本消息回调
     * @param closeReader 通道关闭处理事件
     * @param isRetry 是否进行重连(心跳)
     */
    public WebSocketClient(String url,TextReader textReader,CloseReader closeReader,boolean isRetry){
        try {
            setHostAndPort(url);
            this.textReader = textReader;
            this.closeReader = closeReader;
            this.isRetry = isRetry;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void  setHostAndPort(String url) throws Exception{
        uri = new URI(url);

        String scheme = uri.getScheme() == null? "ws" : uri.getScheme();

        host = uri.getHost() == null? "127.0.0.1" : uri.getHost();

        if (uri.getPort() == -1) {
            if ("ws".equalsIgnoreCase(scheme)) {
                port = 80;
            } else if ("wss".equalsIgnoreCase(scheme)) {
                port = 443;
            } else {
                port = -1;
            }
        } else {
            port = uri.getPort();
        }

        if (!"ws".equalsIgnoreCase(scheme) && !"wss".equalsIgnoreCase(scheme)) {
            log.error("Only WS(S) is supported.");
            System.exit(0);
        }


        if ("wss".equalsIgnoreCase(scheme)) {
            sslCtx = SslContextBuilder.forClient()
                    .trustManager(InsecureTrustManagerFactory.INSTANCE).build();
        } else {
            sslCtx = null;
        }
    }

    private void initBootstrap(TextReader textReader,CloseReader closeReader){
        group = new NioEventLoopGroup();

        handler =
                new WebSocketClientHandler(
                        WebSocketClientHandshakerFactory.newHandshaker(
                                uri, WebSocketVersion.V13, null, true, new DefaultHttpHeaders()),textReader,closeReader);

        bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ChannelPipeline p = ch.pipeline();
                        if (sslCtx != null) {
                            p.addLast(sslCtx.newHandler(ch.alloc(), host, port));
                        }
                        p.addLast(
                                new HttpClientCodec(),
                                new HttpObjectAggregator(8192),
                                WebSocketClientCompressionHandler.INSTANCE,
                                handler);
                    }
                });
    }

    /**
     * 建立连接
     */
    public void start(){
        if(group == null){
            this.restart();
            if(isRetry){
                reconnection();
            }
        }
    }

    /**
     * 重连
     */
    public void restart(){
        try {
            initBootstrap(this.textReader,this.closeReader);
            channel = bootstrap.connect(host, port).sync().channel();
            handler.handshakeFuture().sync();
        } catch (InterruptedException e) {
            log.error("restart error",e);
        }

    }

    private volatile boolean reconnectionThreadStop = false;

    private Thread reconnectionThread;


    private void reconnection(){
        reconnectionThread = new Thread(new Runnable() {
            @Override
            public void run() {

                sleepTime(TimeUnit.MILLISECONDS,5000 - System.currentTimeMillis() % 1000);

                while (!reconnectionThreadStop){
                    if(!channel.isActive()){
                        restart();
                    }else {
                        channel.writeAndFlush(new PingWebSocketFrame());
                    }
                    sleepTime(TimeUnit.SECONDS,15);
                }
            }
        });

        reconnectionThread.setDaemon(true);
        reconnectionThread.start();
    }

    /**
     * 关闭连接
     */
    public void stop(){
        this.reconnectionThreadStop = true;
        sleepTime(TimeUnit.SECONDS,1);
        if (reconnectionThread.getState() != Thread.State.TERMINATED){
            reconnectionThread.interrupt();
            try {
                reconnectionThread.join();
            } catch (InterruptedException e) {
                log.error(e.getMessage(), e);
            }
        }
        channel.close();
        group.shutdownGracefully();
    }

    private void sleepTime(TimeUnit timeUnit,long time){
        try {
            timeUnit.sleep(time);
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }
    }


    public static TextWebSocketFrame getMessage(Message message){
        if(message == null){
            log.error(">>>>>>>>>>>> message can not be empty ");
            return null;
        }
        String result = JSONUtil.toJsonStr(message);
        return new TextWebSocketFrame(result);
    }

    /**
     * 发送消息
     * @param message
     */
    public void sendMsg(Message message){
        if(!channel.isActive()){
            this.restart();
        }
        channel.writeAndFlush(getMessage(message));
    }

}
