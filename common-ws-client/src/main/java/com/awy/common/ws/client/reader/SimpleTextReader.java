package com.awy.common.ws.client.reader;

import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

/**
 * 读取消息
 */
@Slf4j
public class SimpleTextReader implements TextReader {

    @Override
    public void handler(String message, Channel channel) {
        log.info("on message : {}",message);
    }
}
