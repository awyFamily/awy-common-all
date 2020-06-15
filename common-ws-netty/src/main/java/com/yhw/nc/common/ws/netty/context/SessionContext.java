package com.yhw.nc.common.ws.netty.context;

import cn.hutool.core.collection.CollUtil;
import com.yhw.nc.common.ws.netty.constants.ImCommonConstant;
import com.yhw.nc.common.ws.netty.model.ImSession;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yhw
 */
public class SessionContext {

    private static final Map<String, Channel> userChannelMap = new ConcurrentHashMap<>();

    private static final Map<String, ChannelGroup> groupIdChannelGroupMap = new ConcurrentHashMap<>();

    public static void bindSession(ImSession user, Channel channel) {
        if(channel != null){
            userChannelMap.put(user.getUserId(),channel);
            channel.attr(ImCommonConstant.SESSION).set(user);
        }
    }

    public static void unBindSession(Channel channel) {
        if(hasLogin(channel)){
            userChannelMap.remove(getSession(channel).getUserId());
            channel.attr(ImCommonConstant.SESSION).set(null);
        }
    }

    public static boolean hasLogin(Channel channel) {
        return channel.hasAttr(ImCommonConstant.SESSION);
    }

    public static ImSession getSession(Channel channel) {
        return channel.attr(ImCommonConstant.SESSION).get();
    }

    public static Channel getChannel(String userId) {
        return userChannelMap.get(userId);
    }

    /**
     * 通过群组ID  绑定通道组
     * @param groupId 组id
     * @param channelGroup 通道组
     */
    public static void  bindChannelGroup(String groupId,ChannelGroup channelGroup){
        groupIdChannelGroupMap.put(groupId,channelGroup);
    }

    /**
     * 通过 groupId 加 ChannelHandlerContext 绑定 通道组
     * @param groupId 组id
     * @param ctx  channel 上下文
     */
    public static void  bindChannelGroupByGroupId(String groupId, ChannelHandlerContext ctx){
        ChannelGroup channelGroup = getChannelGroup(groupId);
        if(channelGroup == null){
            channelGroup = new DefaultChannelGroup(ctx.executor());
            channelGroup.add(ctx.channel());
            bindChannelGroup(groupId,channelGroup);
        }
        channelGroup.add(ctx.channel());
    }

    /**
     * 通过 ImSession 绑定群组
     * @param session  用户id
     * @param ctx  channel 上下文
     */
    public static void bindChannelGroupBySession(ImSession session, ChannelHandlerContext ctx) {
        if(session != null && ctx != null){
            if(CollUtil.isNotEmpty(session.getGroupIds())){
                for (String groupId : session.getGroupIds()) {
                    bindChannelGroupByGroupId(groupId,ctx);
                }
            }
        }
    }

    /**
     * 解绑所有组 的当前通道
     * @param channel 通道
     */
    public static void unBindChannelGroupByChannel(Channel channel){
        if(channel != null){
            ImSession session = getSession(channel);
            if(session != null && CollUtil.isNotEmpty(session.getGroupIds())){
                for (String groupId : session.getGroupIds()){
                    unBindChannelGroup(groupId,channel);
                }
            }
        }
    }

    /**
     * 解绑指定组的channel
     * @param groupId 组id
     * @param channel 通道
     */
    public static void unBindChannelGroup(String groupId,Channel channel){
        ChannelGroup channelGroup = getChannelGroup(groupId);
        if(channelGroup != null){
            channelGroup.remove(channel);
        }
    }


    /**
     * 根据群组ID 获取通道组
     * @param groupId
     * @return
     */
    public static ChannelGroup getChannelGroup(String groupId){
        return groupIdChannelGroupMap.get(groupId);
    }


    public static Map<String, Channel> getAllChannel(){
        return userChannelMap;
    }

    public static Map<String,ChannelGroup> getAllChannelGroup(){
        return groupIdChannelGroupMap;
    }
}
