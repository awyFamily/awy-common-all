package com.yhw.nc.common.log.listener;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 定义消息实体
 */
@NoArgsConstructor
@Data
public class LogMessage {

    /**
     * 操作事件
     */
    private LocalDateTime optionTime;

    /**
     * 操作方法(类名+方法名称)
     */
    private String methodName;

    /**
     * 日志类型
     */
    private Integer logType;

    /**
     *
     */
    private String optionUsername;

    /**
     * 操作人
     */
    private String optionUser;


    /**
     * 说明
     */
    private String remarks;

    /**
     * 操作用户ip地址
     */
    private String ipAddress;

    public LogMessage(String methodName,Integer LogType,String username,String optionUser,String ipAddress,String remark){
        this(LocalDateTime.now(),methodName,LogType,username,optionUser,ipAddress,remark);
    }

    public LogMessage(LocalDateTime optionTime,String methodName,Integer LogType,String username,String optionUser,String ipAddress,String remark){
        this.optionTime = optionTime;
        this.methodName = methodName;
        this.logType = LogType;
        this.optionUsername = username;
        this.optionUser = optionUser;
        this.ipAddress = ipAddress;
        this.remarks = remark;
    }
}
