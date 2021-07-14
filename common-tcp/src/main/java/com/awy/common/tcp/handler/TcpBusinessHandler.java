package com.awy.common.tcp.handler;

import cn.hutool.core.collection.CollUtil;
import com.awy.common.tcp.codec.BaseMessage;
import com.awy.common.tcp.context.BaseSession;
import com.awy.common.tcp.context.ISessionLifecycle;
import com.awy.common.tcp.context.SessionFactory;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
/**
 * @author yhw
 * @date 2021-07-14
 */
@Slf4j
public class TcpBusinessHandler extends SimpleChannelInboundHandler<BaseMessage> {

    private final ISessionLifecycle lifecycle;
    private final List<IBusinessProcess> processes;

    public TcpBusinessHandler(ISessionLifecycle lifecycle,List<IBusinessProcess> processes){
        this.lifecycle = lifecycle;
        if (CollUtil.isNotEmpty(processes)) {
            processes = processes.stream()
                    .sorted(Comparator.comparing(IBusinessProcess::sortAsc))
                    .collect(Collectors.toList());
        }else {
            processes = new ArrayList<>();
        }
        this.processes = processes;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, BaseMessage msg) throws Exception {
        String clientRemoteAddress = ctx.channel().remoteAddress().toString();
        String clientIp = clientRemoteAddress.replaceAll(".*/(.*):.*", "$1");
        String clientPort = clientRemoteAddress.replaceAll(".*:(.*)", "$1");
        log.info("receive ip:port message : {}:{} . ",clientIp,clientPort);

        BaseSession session = lifecycle.createSession(msg);
        if(session != null){
            if(!SessionFactory.hasLogin(ctx.channel())){
                SessionFactory.bindSession(session,ctx.channel());
            }

            //process business
            for (IBusinessProcess process : processes) {
                if(process.pipeline(msg)){
                    break;
                }
            }

        }else {
            ctx.channel().close();
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        BaseSession session = SessionFactory.getSession(ctx.channel());
        if(session != null){
            lifecycle.unbind(session);
            SessionFactory.unBindSession(ctx.channel());
        }
    }
}
