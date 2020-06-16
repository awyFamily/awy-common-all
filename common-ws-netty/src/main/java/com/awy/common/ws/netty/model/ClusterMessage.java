package com.awy.common.ws.netty.model;

import com.awy.common.ws.netty.packets.Message;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yhw
 */
@NoArgsConstructor
@Data
public class ClusterMessage {

    /**
     * 无状态
     * 单聊
     * 群聊
     */
    private String messageType;

    private List<String> userIdOrGroupIds;

    private Message message;

    public static ClusterMessage noStateMessage(Message message){
        return new ClusterMessage("no",null,message);
    }

    public static ClusterMessage chatMessage(String userId,Message message){
        List<String> userIds = new ArrayList<>(1);
        userIds.add(userId);
        return chatsMessage(userIds,message);
    }

    public static ClusterMessage chatsMessage(List<String> userIds,Message message){
        return new ClusterMessage("chat",userIds,message);
    }

    public static ClusterMessage groupMessage(String groupId,Message message){
        List<String> groupIds = new ArrayList<>(1);
        groupIds.add(groupId);
        return groupsMessage(groupIds,message);
    }

    public static ClusterMessage groupsMessage(List<String> groupIds,Message message){
        return new ClusterMessage("group",groupIds,message);
    }

    public ClusterMessage(String messageType,List<String> userIdOrGroupIds,Message message){
        this.messageType = messageType;
        this.userIdOrGroupIds = userIdOrGroupIds;
        this.message = message;
    }

}
