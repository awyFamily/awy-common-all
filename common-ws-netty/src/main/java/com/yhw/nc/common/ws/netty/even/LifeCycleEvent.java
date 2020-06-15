package com.yhw.nc.common.ws.netty.even;

import com.yhw.nc.common.ws.netty.model.ImSession;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

/**
 * channel 生命周期
 * @author yhw
 */
public interface LifeCycleEvent {

    /**
     * 绑定通道上下文
     * @param login
     * @param ctx
     */
    void bindContext(ImSession login, ChannelHandlerContext ctx);

    /**
     * 解绑通道上下文
     * @param channel
     */
    void cleanContext(Channel channel);
}
