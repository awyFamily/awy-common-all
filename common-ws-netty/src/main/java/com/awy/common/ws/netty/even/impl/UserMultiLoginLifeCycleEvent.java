package com.awy.common.ws.netty.even.impl;

import cn.hutool.core.collection.CollUtil;
import com.awy.common.ws.netty.constants.ImCommonConstant;
import com.awy.common.ws.netty.context.SessionContext;
import com.awy.common.ws.netty.toolkit.IdUtil;
import com.awy.common.ws.netty.even.LifeCycleEvent;
import com.awy.common.ws.netty.model.ImSession;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户多点登录(允许一个用户同时登录多次)
 * 使用 userId 作为一个分组，即用户ID 的 GroupChannel 为一个用户
 * @author yhw
 */
public class UserMultiLoginLifeCycleEvent implements LifeCycleEvent {

    AttributeKey<String> RANDOM_ID = AttributeKey.newInstance("randomId");


    @Override
    public void bindContext(ImSession login, ChannelHandlerContext ctx) {
        //用户分组加用户id
        List<String> groupIds = login.getGroupIds();
        if(CollUtil.isEmpty(groupIds)){
            groupIds = new ArrayList<>(1);
        }
        groupIds.add(login.getUserId());

        Channel channel = ctx.channel();

        //设置用户唯一Id(多点登录使用随机id)
        String userId = IdUtil.randomId();
        channel.attr(RANDOM_ID).set(userId);
        channel.attr(ImCommonConstant.SESSION).set(login);

        //绑定用户
        SessionContext.getAllChannel().put(userId,channel);

        //绑定群组
        SessionContext.bindChannelGroupBySession(login,ctx);

    }

    @Override
    public void cleanContext(Channel channel) {
        //1.通道解绑群组
        SessionContext.unBindChannelGroupByChannel(channel);

        //2.解绑当前通道相关绑定信息
        SessionContext.getAllChannel().remove(channel.attr(RANDOM_ID).get());
        channel.attr(ImCommonConstant.SESSION).set(null);
        channel.attr(RANDOM_ID).set(null);
    }


}
