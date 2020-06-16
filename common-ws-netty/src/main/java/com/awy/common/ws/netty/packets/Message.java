package com.awy.common.ws.netty.packets;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 消息包基类
 * @author yhw
 */
@NoArgsConstructor
@Data
public abstract class Message {

    /**
     * 消息创建时间
     */
    private LocalDateTime createTime = LocalDateTime.now();

    /**
     * 消息唯一id
     */
    private String uid;

    /**
     * 指令代码(约定)
     */
    private Byte cmd;

    /**
     * 暂时定两种(成功 200 , 失败 500)
     */
    private Integer code;


    public Message(Integer code,String uid){
        this.code = code;
        this.uid = uid;
    }

    private void setCmd(){}

    /**
     * 消息指令类型
     * 一般正常会存在3种情况
     * 无状态消息(即做 消息通知)
     * 单聊消息
     * 群聊消息
     * @return
     */
    public abstract Byte getCmd();


}
