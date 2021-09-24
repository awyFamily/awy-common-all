package com.awy.common.tcp;

import com.awy.common.tcp.codec.ITcpDecoder;
import com.awy.common.tcp.codec.ITcpEncoder;
import com.awy.common.tcp.context.ISessionLifecycle;
import com.awy.common.tcp.handler.IBusinessProcess;
import com.awy.common.tcp.handler.TcpBusinessHandler;
import com.awy.common.tcp.handler.TcpCodecHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioChannelOption;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author yhw
 * @date 2021-07-14
 */
@Slf4j
public class TcpServer {

    private boolean ssl = false;
    private int port;
    private ServerBootstrap serverBootstrap;

    private TcpServer(){}

    public TcpServer(int port, boolean ssl, ITcpDecoder decoder, ITcpEncoder encoder, ISessionLifecycle lifecycle, List<IBusinessProcess> processes){
        this.ssl = ssl;
        this.port = port;

        ChannelInitializer<SocketChannel> initializer = new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel channel) {
                log.info("channel initialized: {}", channel);
                channel.pipeline().addLast(new LoggingHandler(LogLevel.TRACE));
                //请求编码 响应解码
                channel.pipeline().addLast(new TcpCodecHandler(decoder, encoder));
                channel.pipeline().addLast(new TcpBusinessHandler(lifecycle,processes));
            }
        };

        //主从 react 模型
        serverBootstrap = newServerBootstrap();
        //绑定处理器
        serverBootstrap.option(NioChannelOption.SO_BACKLOG, 1024)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .childOption(NioChannelOption.TCP_NODELAY, true)
                .handler(new LoggingHandler(LogLevel.DEBUG))
                .childHandler(initializer);;
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
                    (ssl ? "https" : "http") + "://127.0.0.1:" + port);
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
