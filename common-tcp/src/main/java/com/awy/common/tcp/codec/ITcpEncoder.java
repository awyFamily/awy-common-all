package com.awy.common.tcp.codec;

import io.netty.buffer.ByteBuf;
/**
 * @author yhw
 * @date 2021-07-14
 */
public interface ITcpEncoder<T extends BaseMessage> {

    ByteBuf encode(T message, ByteBuf buffer);
}
