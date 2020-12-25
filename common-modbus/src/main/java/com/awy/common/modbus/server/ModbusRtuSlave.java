package com.awy.common.modbus.server;

import com.awy.common.modbus.server.context.ModBusFutureContext;
import com.awy.common.modbus.server.context.ModbusSession;
import com.awy.common.modbus.server.context.SessionContext;
import com.awy.common.modbus.server.rtu.ServiceRequestRtuHandler;
import com.codahale.metrics.Counter;
import com.digitalpetri.modbus.ExceptionCode;
import com.digitalpetri.modbus.ModbusPdu;
import com.digitalpetri.modbus.codec.ModbusRequestEncoder;
import com.digitalpetri.modbus.codec.ModbusResponseDecoder;
import com.digitalpetri.modbus.codec.rtu.ModbusRtuCodec;
import com.digitalpetri.modbus.codec.rtu.ModbusRtuPayload;
import com.digitalpetri.modbus.requests.ModbusRequest;
import com.digitalpetri.modbus.responses.ExceptionResponse;
import com.digitalpetri.modbus.responses.HeartbeatResponse;
import com.digitalpetri.modbus.responses.ModbusResponse;
import com.digitalpetri.modbus.responses.RegistersAuthResponse;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioChannelOption;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;

import java.net.SocketAddress;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author yhw
 */
@Slf4j
public class ModbusRtuSlave {

    private final AtomicReference<ServiceRequestRtuHandler> requestHandler =
            new AtomicReference<>(new ServiceRequestRtuHandler() {});

    private final Map<SocketAddress, Channel> serverChannels = new ConcurrentHashMap<>();

    private final Counter channelCounter = new Counter();

    private final ServerBootstrap bootstrap;


    public ModbusRtuSlave() {
        bootstrap = newServerBootstrap();
    }

    public CompletableFuture<ModbusRtuSlave> bind(String host, int port) {
        CompletableFuture<ModbusRtuSlave> bindFuture = new CompletableFuture<>();

        ChannelInitializer<SocketChannel> initializer = new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel channel) {
                channelCounter.inc();
                log.info("channel initialized: {}", channel);
                channel.pipeline().addLast(new LoggingHandler(LogLevel.TRACE));
                //请求编码 响应解码
                channel.pipeline().addLast(new ModbusRtuCodec(new ModbusRequestEncoder(), new ModbusResponseDecoder()));
                channel.pipeline().addLast(new ModbusRtuSlaveHandler(ModbusRtuSlave.this));

                channel.closeFuture().addListener(future -> channelCounter.dec());
            }
        };

        bootstrap.option(NioChannelOption.SO_BACKLOG, 1024)
                .childOption(NioChannelOption.TCP_NODELAY, true)
                .handler(new LoggingHandler(LogLevel.DEBUG))
                .childHandler(initializer)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);

        bootstrap.bind(port).addListener((ChannelFuture future) -> {
            if (future.isSuccess()) {
                Channel channel = future.channel();
                serverChannels.put(channel.localAddress(), channel);
                bindFuture.complete(ModbusRtuSlave.this);
                log.info("bind port : " + port + " success");
            } else {
                bindFuture.completeExceptionally(future.cause());
                log.info("bind port : " + port + " fail.....");
            }
        });

        return bindFuture;
    }

    private ServerBootstrap newServerBootstrap() {
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

    public void setRequestHandler(ServiceRequestRtuHandler requestHandler) {
        this.requestHandler.set(requestHandler);
    }

    public void shutdown() {
        serverChannels.values().forEach(Channel::close);
        serverChannels.clear();
        bootstrap.config().group().shutdownGracefully();
        bootstrap.config().childGroup().shutdownGracefully();
        SessionContext.getAllChannel().clear();
    }


    /**
     * 读取消息
     * @param ctx
     * @param payload
     */
    private void onChannelRead(ChannelHandlerContext ctx, ModbusRtuPayload payload) {
        ServiceRequestRtuHandler handler = requestHandler.get();
        if (handler == null) return;

        //complete response
        ModBusFutureContext.completeResponse(ctx,payload);
        switch (payload.getModbusPdu().getFunctionCode()) {
            case ReadCoils:
                handler.onReadCoils(ModbusRtuServiceRequest.of(payload, ctx.channel()));
                break;

            case ReadDiscreteInputs:
                handler.onReadDiscreteInputs(ModbusRtuServiceRequest.of(payload, ctx.channel()));
                break;

            case ReadHoldingRegisters:
                handler.onReadHoldingRegisters(ModbusRtuServiceRequest.of(payload, ctx.channel()));
                break;

            case ReadInputRegisters:
                handler.onReadInputRegisters(ModbusRtuServiceRequest.of(payload, ctx.channel()));
                break;

            case WriteSingleCoil:
                handler.onWriteSingleCoil(ModbusRtuServiceRequest.of(payload, ctx.channel()));
                break;

            case WriteSingleRegister:
                handler.onWriteSingleRegister(ModbusRtuServiceRequest.of(payload, ctx.channel()));
                break;

            case WriteMultipleCoils:
                handler.onWriteMultipleCoils(ModbusRtuServiceRequest.of(payload, ctx.channel()));
                break;

            case WriteMultipleRegisters:
                handler.onWriteMultipleRegisters(ModbusRtuServiceRequest.of(payload, ctx.channel()));
                break;

            case MaskWriteRegister:
                handler.onMaskWriteRegister(ModbusRtuServiceRequest.of(payload, ctx.channel()));
                break;

            case ReadWriteMultipleRegisters:
                handler.onReadWriteMultipleRegisters(ModbusRtuServiceRequest.of(payload, ctx.channel()));
                break;
            case EquipmentRegister:
                log.info("receive equipment register package .......");
                RegistersAuthResponse registersAuth = (RegistersAuthResponse)payload.getModbusPdu();
                ModbusSession session = new ModbusSession(payload.getSiteId(),registersAuth.getManufacturer(),registersAuth.getEquipmentSerialNumber());
                SessionContext.bindSession(session,ctx.channel());
//                handler.onEquipmentRegisterRequest(payload);
                break;
            case Heartbeat:
                log.info("receive Heartbeat package .......");
                HeartbeatResponse heartbeatResponse = (HeartbeatResponse)payload.getModbusPdu();
                handler.onHeartbeatRequest(payload.getSiteId(),heartbeatResponse);
                break;

            default:
                /* Function code not currently supported */
                ExceptionResponse response = new ExceptionResponse(
                        payload.getModbusPdu().getFunctionCode(),
                        ExceptionCode.IllegalFunction);
                ctx.writeAndFlush(new ModbusRtuPayload(payload.getSiteId(),  response));
                break;
        }
    }

    private void onChannelInactive(ChannelHandlerContext ctx) {
        log.error("Master/client channel closed: {}", ctx.channel());
    }

    private void onExceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("Exception caught on channel: {}", ctx.channel(), cause);
        ctx.close();
    }

    @Slf4j
    private static class ModbusRtuSlaveHandler extends SimpleChannelInboundHandler<ModbusRtuPayload> {


        private final ModbusRtuSlave slave;

        private ModbusRtuSlaveHandler(ModbusRtuSlave slave) {
            this.slave = slave;
        }

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, ModbusRtuPayload msg) {
            String clientRemoteAddress = ctx.channel().remoteAddress().toString();
            String clientIp = clientRemoteAddress.replaceAll(".*/(.*):.*", "$1");
            String clientPort = clientRemoteAddress.replaceAll(".*:(.*)", "$1");
            log.info("receive ip:port message : {}:{} . siteId : {} . functionCode : {} ",clientIp,clientPort,msg.getSiteId(),msg.getModbusPdu().getFunctionCode().toString());

            slave.onChannelRead(ctx, msg);
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) {
            slave.onChannelInactive(ctx);
            SessionContext.unBindSession(ctx.channel());
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            slave.onExceptionCaught(ctx, cause);
        }

    }

    private static class ModbusRtuServiceRequest<Request extends ModbusResponse, Response extends ModbusRequest>
            implements ServiceRequestRtuHandler.ServiceRequestRtu<Request, Response> {

        private final int  siteId;
        private final Request request;
        private final Channel channel;

        private ModbusRtuServiceRequest(int siteId,  Request request, Channel channel) {
            this.siteId = siteId;
            this.request = request;
            this.channel = channel;
        }

        @Override
        public int getSiteId() {
            return siteId;
        }
        @Override
        public Request getRequest() {
            return request;
        }

        @Override
        public Channel getChannel() {
            return channel;
        }

        @Override
        public void sendResponse(Response response) {
            channel.writeAndFlush(new ModbusRtuPayload(siteId, response));
        }

        @Override
        public void sendException(ExceptionCode exceptionCode) {
            ExceptionResponse response = new ExceptionResponse(request.getFunctionCode(), exceptionCode);

            channel.writeAndFlush(new ModbusRtuPayload(siteId, response));
        }

        @SuppressWarnings("unchecked")
        public static <Request extends ModbusResponse, Response extends ModbusRequest>
        ModbusRtuServiceRequest<Request, Response> of(ModbusRtuPayload payload, Channel channel) {

            return new ModbusRtuServiceRequest<>(
                    payload.getSiteId(),
                    (Request) payload.getModbusPdu(),
                    channel
            );
        }


    }

}

