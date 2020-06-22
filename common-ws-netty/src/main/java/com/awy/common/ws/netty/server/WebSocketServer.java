package com.awy.common.ws.netty.server;

import com.awy.common.ws.netty.cluster.ImClusterTopic;
import com.awy.common.ws.netty.context.GlobalContent;
import com.awy.common.ws.netty.even.LifeCycleEvent;
import com.awy.common.ws.netty.even.impl.SimpleLifeCycleEvent;
import com.awy.common.ws.netty.process.AuthProcess;
import com.awy.common.ws.netty.process.SimpleAuthProcess;
import com.awy.common.ws.netty.server.handler.WebSocketServerInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yhw
 */
@Slf4j
public class WebSocketServer {

    /**
     * 是否启用ssl
     */
    private boolean ssl = false;
    private int port;
    private String websocketPath;

    private ServerBootstrap serverBootstrap;


    private WebSocketServer(){}


    public WebSocketServer(int port, String websocketPath, boolean ssl, AuthProcess authProcess, LifeCycleEvent lifeCycleEvent, ImClusterTopic imClusterTopic){
        this.ssl = ssl;
        this.port = port;
        this.websocketPath = websocketPath;

        //设置全局上下文
        if(authProcess == null){
            authProcess = new SimpleAuthProcess();
        }
        if( lifeCycleEvent == null){
            lifeCycleEvent = new SimpleLifeCycleEvent();
        }
        GlobalContent globalContent = GlobalContent.getInstance();
        globalContent.setAuthProcess(authProcess);
        globalContent.setImClusterTopic(imClusterTopic);
        globalContent.setLifeCycleEvent(lifeCycleEvent);

        //主从 react 模型
        serverBootstrap = newServerBootstrap();
        //绑定处理器
        serverBootstrap.childHandler(new WebSocketServerInitializer(getSslContext(),websocketPath));
    }


    public ServerBootstrap newServerBootstrap() {
        //默认开启 Epoll
        if (Epoll.isAvailable()) {
            EventLoopGroup bossGroup =
                    new EpollEventLoopGroup(1, new DefaultThreadFactory("bossGroup", true));
            EventLoopGroup workerGroup =
                    new EpollEventLoopGroup(0, new DefaultThreadFactory("workerGroup", true));
            return new ServerBootstrap().group(bossGroup, workerGroup).channel(EpollServerSocketChannel.class);
        }

        return newNioServerBootstrap(1, 0);
    }

    private ServerBootstrap newNioServerBootstrap(int bossThreads, int workerThreads) {
        EventLoopGroup bossGroup;
        EventLoopGroup workerGroup;
        if (bossThreads >= 0 && workerThreads >= 0) {
            bossGroup = new NioEventLoopGroup(bossThreads);
            workerGroup = new NioEventLoopGroup(workerThreads);
        } else {
            bossGroup = new NioEventLoopGroup();
            workerGroup = new NioEventLoopGroup();
        }

        return new ServerBootstrap().group(bossGroup, workerGroup).channel(NioServerSocketChannel.class);
    }


    private SslContext getSslContext(){
        SslContext sslCtx = null;
        try{
            if (this.ssl) {
                SelfSignedCertificate ssc = new SelfSignedCertificate();
                sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return sslCtx;
    }

    public void start(){
        try {
            serverBootstrap.bind(port).sync().channel();

           log.info("Open your web browser and navigate to " +
                    (ssl ? "https" : "http") + "://127.0.0.1:" + port + "" + websocketPath);

//            ch.closeFuture().sync();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void stop(){
        serverBootstrap.config().group().shutdownGracefully();
        serverBootstrap.config().childGroup().shutdownGracefully();
    }

}
