package com.awy.common.message.api.constants;

public interface MessageConstant {

    /**
     * 异常消息
     */
    Integer ERROR_CODE = 500;

    /**
     * 消费发送成功响应包
     */
    Integer SUCCESS_CODE = 200;

    /**
     * 无需处理(无状态消息)
     */
    byte NOT_STATE_CODE = 0;

    /**
     * 单聊消息
     */
    byte CHAR_CODE = 1;

    /**
     * 群聊消息
     */
    byte GROUP_CODE = 2;
}
