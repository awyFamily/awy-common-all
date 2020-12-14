package com.awy.common.modbus.server;

import com.awy.common.modbus.server.context.SessionContext;
import com.codahale.metrics.Counter;
import com.digitalpetri.modbus.ExceptionCode;
import com.digitalpetri.modbus.codec.ModbusRequestDecoder;
import com.digitalpetri.modbus.codec.ModbusResponseEncoder;
import com.digitalpetri.modbus.codec.tcp.ModbusTcpCodec;
import com.digitalpetri.modbus.codec.tcp.ModbusTcpPayload;
import com.digitalpetri.modbus.requests.ModbusRequest;
import com.digitalpetri.modbus.responses.ExceptionResponse;
import com.digitalpetri.modbus.responses.ModbusResponse;
import com.digitalpetri.modbus.slave.ServiceRequestHandler;
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

@Slf4j
public class ModBusTcpSlave {


    private final AtomicReference<ServiceRequestHandler> requestHandler =
            new AtomicReference<>(new ServiceRequestHandler() {});

    private final Map<SocketAddress, Channel> serverChannels = new ConcurrentHashMap<>();

    private final Counter channelCounter = new Counter();

    private final ServerBootstrap bootstrap;

    public ModBusTcpSlave() {
        this.bootstrap = newServerBootstrap();
    }

    public CompletableFuture<ModBusTcpSlave> bind(String host, int port) {
        CompletableFuture<ModBusTcpSlave> bindFuture = new CompletableFuture<>();

        ChannelInitializer<SocketChannel> initializer = new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel channel) {
                channelCounter.inc();
                log.info("channel initialized: {}", channel);
                channel.pipeline().addLast(new LoggingHandler(LogLevel.TRACE));
                //响应编码 请求解码
                channel.pipeline().addLast(new ModbusTcpCodec(new ModbusResponseEncoder(), new ModbusRequestDecoder()));
                channel.pipeline().addLast(new ModbusTcpSlaveHandler(ModBusTcpSlave.this));

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
                bindFuture.complete(ModBusTcpSlave.this);
                log.info("tcp bind port : " + port + " success");
            } else {
                bindFuture.completeExceptionally(future.cause());
                log.info("tcp bind port : " + port + " fail");
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

    public void setRequestHandler(ServiceRequestHandler requestHandler) {
        this.requestHandler.set(requestHandler);
    }

    public void shutdown() {
        serverChannels.values().forEach(Channel::close);
        serverChannels.clear();
    }

    private void onChannelRead(ChannelHandlerContext ctx, ModbusTcpPayload payload) {
        ServiceRequestHandler handler = requestHandler.get();
        if (handler == null) return;

        switch (payload.getModbusPdu().getFunctionCode()) {
            case ReadCoils:
                handler.onReadCoils(ModbusTcpServiceRequest.of(payload, ctx.channel()));
                break;

            case ReadDiscreteInputs:
                handler.onReadDiscreteInputs(ModbusTcpServiceRequest.of(payload, ctx.channel()));
                break;

            case ReadHoldingRegisters:
                handler.onReadHoldingRegisters(ModbusTcpServiceRequest.of(payload, ctx.channel()));
                break;

            case ReadInputRegisters:
                handler.onReadInputRegisters(ModbusTcpServiceRequest.of(payload, ctx.channel()));
                break;

            case WriteSingleCoil:
                handler.onWriteSingleCoil(ModbusTcpServiceRequest.of(payload, ctx.channel()));
                break;

            case WriteSingleRegister:
                handler.onWriteSingleRegister(ModbusTcpServiceRequest.of(payload, ctx.channel()));
                break;

            case WriteMultipleCoils:
                handler.onWriteMultipleCoils(ModbusTcpServiceRequest.of(payload, ctx.channel()));
                break;

            case WriteMultipleRegisters:
                handler.onWriteMultipleRegisters(ModbusTcpServiceRequest.of(payload, ctx.channel()));
                break;

            case MaskWriteRegister:
                handler.onMaskWriteRegister(ModbusTcpServiceRequest.of(payload, ctx.channel()));
                break;

            case ReadWriteMultipleRegisters:
                handler.onReadWriteMultipleRegisters(ModbusTcpServiceRequest.of(payload, ctx.channel()));
                break;

            default:
                /* Function code not currently supported */
                ExceptionResponse response = new ExceptionResponse(
                        payload.getModbusPdu().getFunctionCode(),
                        ExceptionCode.IllegalFunction);

                ctx.writeAndFlush(new ModbusTcpPayload(payload.getTransactionId(), payload.getUnitId(), response));
                break;
        }
    }

    private void onChannelInactive(ChannelHandlerContext ctx) {
        log.debug("Master/client channel closed: {}", ctx.channel());
    }

    private void onExceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("Exception caught on channel: {}", ctx.channel(), cause);
        ctx.close();
    }

    private static class ModbusTcpSlaveHandler extends SimpleChannelInboundHandler<ModbusTcpPayload> {

        private final ModBusTcpSlave slave;

        private ModbusTcpSlaveHandler(ModBusTcpSlave slave) {
            this.slave = slave;
        }

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, ModbusTcpPayload msg) {
            String clientRemoteAddress = ctx.channel().remoteAddress().toString();
            String clientIp = clientRemoteAddress.replaceAll(".*/(.*):.*", "$1");
            String clientPort = clientRemoteAddress.replaceAll(".*:(.*)", "$1");
            log.info("receive ip:port message : {}:{} . siteId : {} . functionCode : {} ",clientIp,clientPort,msg.getUnitId(),msg.getModbusPdu().getFunctionCode().toString());

            slave.onChannelRead(ctx, msg);
            SessionContext.bindSession((int)msg.getUnitId(),ctx.channel());
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

    private static class ModbusTcpServiceRequest<Request extends ModbusRequest, Response extends ModbusResponse>
            implements ServiceRequestHandler.ServiceRequest<Request, Response> {

        private final short transactionId;
        private final short unitId;
        private final Request request;
        private final Channel channel;

        private ModbusTcpServiceRequest(short transactionId, short unitId, Request request, Channel channel) {
            this.transactionId = transactionId;
            this.unitId = unitId;
            this.request = request;
            this.channel = channel;
        }

        @Override
        public short getTransactionId() {
            return transactionId;
        }

        @Override
        public short getUnitId() {
            return unitId;
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
            channel.writeAndFlush(new ModbusTcpPayload(transactionId, unitId, response));
        }

        @Override
        public void sendException(ExceptionCode exceptionCode) {
            ExceptionResponse response = new ExceptionResponse(request.getFunctionCode(), exceptionCode);

            channel.writeAndFlush(new ModbusTcpPayload(transactionId, unitId, response));
        }

        @SuppressWarnings("unchecked")
        public static <Request extends ModbusRequest, Response extends ModbusResponse>
        ModbusTcpServiceRequest<Request, Response> of(ModbusTcpPayload payload, Channel channel) {

            return new ModbusTcpServiceRequest<>(
                    payload.getTransactionId(),
                    payload.getUnitId(),
                    (Request) payload.getModbusPdu(),
                    channel
            );
        }

    }

}

