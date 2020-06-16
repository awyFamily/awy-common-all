package com.awy.common.ws.netty.constants;

import com.awy.common.ws.netty.model.ImSession;
import io.netty.util.AttributeKey;

/**
 * @author yhw
 */
public interface ImCommonConstant {

    /**
     * 消息体包扫描路径
     */
    String IM_NT_PREFIX = "im.ws";

    String DEFAULT_WEBSOCKET_PATH = "/websocket";


    String  HANDSHAKER = "HANDSHAKER";


    AttributeKey<ImSession> SESSION = AttributeKey.newInstance("session");

    /**
     * 异常消息
     */
    Integer ERROR_CODE = 500;

    /**
     * 消费发送成功响应包
     */
    Integer SUCCESS_CODE = 200;

    /**
     * 无需处理
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
