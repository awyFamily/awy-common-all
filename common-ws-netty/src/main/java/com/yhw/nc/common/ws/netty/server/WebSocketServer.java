package com.yhw.nc.common.ws.netty.server;

import com.yhw.nc.common.ws.netty.cluster.ImClusterTopic;
import com.yhw.nc.common.ws.netty.context.GlobalContent;
import com.yhw.nc.common.ws.netty.even.LifeCycleEvent;
import com.yhw.nc.common.ws.netty.even.impl.SimpleLifeCycleEvent;
import com.yhw.nc.common.ws.netty.process.AuthProcess;
import com.yhw.nc.common.ws.netty.process.SimpleAuthProcess;
import com.yhw.nc.common.ws.netty.server.handler.WebSocketServerInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;

/**
 * @author yhw
 */
public class WebSocketServer {

    /**
     * 是否启用ssl
     */
    private boolean ssl = false;
    private int port;
    private String websocketPath;


    private ServerBootstrap serverBootstrap;
    private NioEventLoopGroup boss;
    private NioEventLoopGroup work;


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
        serverBootstrap = new ServerBootstrap();
        boss = new NioEventLoopGroup(1);
        work = new NioEventLoopGroup();

        serverBootstrap.group(boss,work)
                .channel(NioServerSocketChannel.class)
                .childHandler(new WebSocketServerInitializer(getSslContext(),websocketPath));
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
            Channel ch = serverBootstrap.bind(port).sync().channel();

            System.out.println("Open your web browser and navigate to " +
                    (ssl ? "https" : "http") + "://127.0.0.1:" + port + "" + websocketPath);

//            ch.closeFuture().sync();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void stop(){
        boss.shutdownGracefully();
        work.shutdownGracefully();
    }
}
