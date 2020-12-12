/*
 * Copyright 2016 Kevin Herron
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.digitalpetri.modbus.codec.tcp;

import com.digitalpetri.modbus.ModbusPdu;
import com.digitalpetri.modbus.UnsupportedPdu;
import com.digitalpetri.modbus.codec.ModbusPduDecoder;
import com.digitalpetri.modbus.codec.ModbusPduEncoder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ModbusTcpCodec extends ByteToMessageCodec<ModbusTcpPayload> {

    private static final int HeaderLength = MbapHeader.LENGTH;
    private static final int HeaderSize = 6;
    private static final int LengthFieldIndex = 4;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final ModbusPduEncoder encoder;
    private final ModbusPduDecoder decoder;

    public ModbusTcpCodec(ModbusPduEncoder encoder, ModbusPduDecoder decoder) {
        this.encoder = encoder;
        this.decoder = decoder;
    }

    //编码
    @Override
    protected void encode(ChannelHandlerContext ctx, ModbusTcpPayload payload, ByteBuf buffer) {
        int headerStartIndex = buffer.writerIndex();
        buffer.writeZero(MbapHeader.LENGTH);

        int pduStartIndex = buffer.writerIndex();
        encoder.encode(payload.getModbusPdu(), buffer);
        int pduLength = buffer.writerIndex() - pduStartIndex;

        MbapHeader header = new MbapHeader(
            payload.getTransactionId(),
            pduLength + 1,
            payload.getUnitId()
        );

        int currentWriterIndex = buffer.writerIndex();
        buffer.writerIndex(headerStartIndex);
        MbapHeader.encode(header, buffer);
        buffer.writerIndex(currentWriterIndex);
    }

    //解码
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> out) throws Exception {
        //起始读取的下标
        int startIndex = buffer.readerIndex();

        //可读字节数 >= 7  并且  可读字节数 >= 长度属性内容 + HeaderSize
        while (buffer.readableBytes() >= HeaderLength &&
            buffer.readableBytes() >= getLength(buffer, startIndex) + HeaderSize) {

            try {
                MbapHeader mbapHeader = MbapHeader.decode(buffer);
                //控制类型
                ModbusPdu modbusPdu = decoder.decode(buffer);
                //跳出读取(指令错误)
                if (modbusPdu instanceof UnsupportedPdu) {
                    // Advance past any bytes we should have read but didn't...
                    int endIndex = startIndex + getLength(buffer, startIndex) + 6;
                    //设置可读取指标为 结束下标
                    buffer.readerIndex(endIndex);
                }
                out.add(new ModbusTcpPayload(mbapHeader.getTransactionId(), mbapHeader.getUnitId(), modbusPdu));
            } catch (Throwable t) {
                throw new Exception("error decoding header/pdu", t);
            }

            //返回缓冲区可读的字节下标
            startIndex = buffer.readerIndex();
        }
    }

    /**
     *  读取 16 字节的数据
     * @param in ByteBuf
     * @param startIndex 起始下标(从第5个下标开始读) 读 2个字节
     * @return  map 报文头的 长度属性
     */
    private int getLength(ByteBuf in, int startIndex) {
        return in.getUnsignedShort(startIndex + LengthFieldIndex);
    }

}
