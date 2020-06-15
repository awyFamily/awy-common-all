package com.yhw.nc.common.ws.netty.toolkit;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import com.yhw.nc.common.ws.netty.cluster.ImClusterTopic;
import com.yhw.nc.common.ws.netty.config.ImConfig;
import com.yhw.nc.common.ws.netty.context.GlobalContent;
import com.yhw.nc.common.ws.netty.context.SessionContext;
import com.yhw.nc.common.ws.netty.model.ClusterMessage;
import com.yhw.nc.common.ws.netty.packets.Message;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 *  1.如果是单机(直接发送)
 *  2.如果是集群(发送到队列-> 队列转发到节点)
 * @author yhw
 */
@Slf4j
public class ImSendUtil {

    /**
     * 发送给指定用户
     * @param userId 用户id
     * @param message 消息体
     */
    public static void sendUser(String userId,Message message){
        if(isCluster()){
            getImClusterTopic().publish(ClusterMessage.chatMessage(userId,message));
        }else {
            sendCurrentNodeUser(userId,message);
        }
    }

    /**
     * 发送给指定用户列表
     * @param userIds 用户id列表
     * @param message 消息体
     */
    public static void sendUsers(List<String> userIds,Message message){
        if(isCluster()){
            getImClusterTopic().publish(ClusterMessage.chatsMessage(userIds,message));
        }else {
            sendCurrentNodeUsers(userIds,message);
        }
    }

    /**
     * 发送指定群组
     * @param groupId 群组id
     * @param message 消息体
     */
    public static void sendGroup(String groupId,Message message){
        if(isCluster()){
            getImClusterTopic().publish(ClusterMessage.groupMessage(groupId,message));
        }else {
            sendCurrentNodeGroup(groupId,message);
        }
    }

    /**
     * 发送指定群组列表
     * @param groupIds 群组列表
     * @param message 消息体
     */
    public static void sendGroups(List<String> groupIds,Message message){
        if(isCluster()){
            getImClusterTopic().publish(ClusterMessage.groupsMessage(groupIds,message));
        }else {
            sendCurrentNodeGroups(groupIds,message);
        }
    }

    /**
     * 发送给全部用户
     * @param message
     */
    public static void sendAll(Message message){
        //if cluster
        if(isCluster()){
            getImClusterTopic().publish(ClusterMessage.noStateMessage(message));
        }else {
            //if standalone
            sendCurrentNodeAllChannel(message);
        }
    }

    /**
     * 发送给当前节点指定用户
     * @param userId 用户id
     * @param message 消息
     */
    public static void sendCurrentNodeUser(String userId,Message message){
        Channel channel = SessionContext.getChannel(userId);
        if(channel != null){
            channel.writeAndFlush(getMessage(message));
        }
    }

    /**
     * 发送给当前节点指定用户列表
     * @param userIds 用户id 列表
     * @param message 消息体
     */
    public static void sendCurrentNodeUsers(List<String> userIds,Message message){
        if(CollUtil.isNotEmpty(userIds)){
            if(userIds.size() == 1){
                sendCurrentNodeUser(userIds.get(0),message);
            }else {
                for (String userId : userIds) {
                    sendCurrentNodeUser(userId,message);
                }
            }
        }
    }

    /**
     * 发送给当前节点指定群组
     * @param groupId
     * @param message
     */
    public static void sendCurrentNodeGroup(String groupId,Message message){
        ChannelGroup channelGroup = SessionContext.getChannelGroup(groupId);
        if(channelGroup != null){
            channelGroup.writeAndFlush(getMessage(message));
        }
    }

    /**
     * 发送给当前节点指定群组列表
     * @param groupIds 群组id列表
     * @param message 消息
     */
    public static void sendCurrentNodeGroups(List<String> groupIds, Message message){
        if(CollUtil.isNotEmpty(groupIds)){
            if(groupIds.size() == 1){
                sendCurrentNodeGroup(groupIds.get(0),message);
            }else {
                for (String groupId : groupIds) {
                    sendCurrentNodeGroup(groupId,message);
                }
            }
        }
    }

    /**
     * 发送给当前节点所有用户
     * @param message 消息
     */
    public static void sendCurrentNodeAllChannel(Message message){
        for (Map.Entry<String, Channel> entry : SessionContext.getAllChannel().entrySet()){
            entry.getValue().writeAndFlush(getMessage(message));
        }
    }


    /**
     * 是否多节点
     * @return
     */
    private static boolean isCluster(){
        return ImConfig.getImConfig().getPropertiesConfig().isCluster();
    }

    /**
     * 获取节点推送主题
     * @return
     */
    private static ImClusterTopic getImClusterTopic(){
        return GlobalContent.getInstance().getImClusterTopic();
    }

    /**
     * 获取消息体
     * @param message 消息体
     * @return webSocket 消息体
     */
    public static TextWebSocketFrame getMessage(Message message){
        if(message == null){
            log.error(">>>>>>>>>>>> message can not be empty ");
            return null;
        }
        String result = JSONUtil.toJsonStr(message);
        return new TextWebSocketFrame(result.toUpperCase(Locale.US));
    }

}
