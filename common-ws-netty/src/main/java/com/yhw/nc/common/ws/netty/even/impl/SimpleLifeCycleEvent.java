package com.yhw.nc.common.ws.netty.even.impl;

import com.yhw.nc.common.ws.netty.context.SessionContext;
import com.yhw.nc.common.ws.netty.even.LifeCycleEvent;
import com.yhw.nc.common.ws.netty.model.ImSession;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

/**
 * 简单生命周期(一个用户只允许登录一次)
 * @author yhw
 */
public class SimpleLifeCycleEvent implements LifeCycleEvent {


    /**
     * 绑定上下文信息
     * @param login
     * @param ctx
     */
    @Override
    public void bindContext(ImSession login, ChannelHandlerContext ctx){
        //通道绑定登录信息
        SessionContext.bindSession(login,ctx.channel());
        //绑定群组信息
        SessionContext.bindChannelGroupBySession(login,ctx);



        //上线相关操作
    }

    /**
     * 解绑操作
     * @param channel
     */
    @Override
    public void cleanContext(Channel channel){
        //1.解绑通道群组信息
        SessionContext.unBindChannelGroupByChannel(channel);
        //2.解绑当前通道相关绑定信息
        SessionContext.unBindSession(channel);



        //离线相关操作
    }
}
