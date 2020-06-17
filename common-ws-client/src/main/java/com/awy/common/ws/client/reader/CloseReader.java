package com.awy.common.ws.client.reader;

/**
 * 当服务器断开事件
 */
public interface CloseReader {

    /**
     * 关闭连接事件
     */
    void onClose();
}
