package com.awy.common.ws.netty.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Im用户
 * @author yhw
 */
@NoArgsConstructor
@Data
public class ImSession {

    /**
     * 用户唯一标识
     */
    private String userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 群组信息
     */
    private List<String> groupIds;

    /**
     * 是否在线
     * 0-在线  1-离线
     */
    private Byte online;


    public ImSession(String userId,String username,Byte online){
        this.userId = userId;
        this.username = username;
        this.online = online;
    }

}
