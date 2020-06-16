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

}
