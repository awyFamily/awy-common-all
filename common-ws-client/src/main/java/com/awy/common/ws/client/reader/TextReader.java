package com.awy.common.ws.client.reader;

import io.netty.channel.Channel;

/**
 * 读取文本文件
 */
public interface TextReader {

    /**
     * 处理文本事件
     * @param message
     * @param channel
     */
    void handler(String message, Channel channel);


}
