package com.awy.common.tcp.codec;

import io.netty.buffer.ByteBuf;
/**
 * @author yhw
 * @date 2021-07-14
 */
public interface ITcpDecoder<T extends BaseMessage> {

    T decode(ByteBuf buffer);
}
