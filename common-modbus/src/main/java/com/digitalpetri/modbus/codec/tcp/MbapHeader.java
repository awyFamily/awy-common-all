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

import io.netty.buffer.ByteBuf;

/**
 * modbus-tcp  map 报文头的
 */
public class MbapHeader {

    public static final int PROTOCOL_ID = 0;
    public static final int LENGTH = 7;

    //交易识别码(2 byte)
    private final short transactionId;
    //协议标识符(2 byte)
    private final int protocolId;
    //长度(2 byte)
    private final int length;
    //单位标识符(1 byte) 可以理解为设备地址
    private final short unitId;

    public MbapHeader(short transactionId, int length, short unitId) {
        this(transactionId, PROTOCOL_ID, length, unitId);
    }

    public MbapHeader(short transactionId, int protocolId, int length, short unitId) {
        this.transactionId = transactionId;
        this.protocolId = protocolId;
        this.length = length;
        this.unitId = unitId;
    }

    public short getTransactionId() {
        return transactionId;
    }

    public int getProtocolId() {
        return protocolId;
    }

    public int getLength() {
        return length;
    }

    public short getUnitId() {
        return unitId;
    }

    public static MbapHeader decode(ByteBuf buffer) {
        return new MbapHeader(
                //并将readIndex增加2
                buffer.readShort(),
                //将当前readIndex处的无符号short值作为int值返回，并将readIndex增加2
                buffer.readUnsignedShort(),
                buffer.readUnsignedShort(),
                //将当前readIndex处的无符号字节值作为short返回，并将readIndex增加1
                buffer.readUnsignedByte()
        );
    }

    public static ByteBuf encode(MbapHeader header, ByteBuf buffer) {
        buffer.writeShort(header.transactionId);
        buffer.writeShort(header.protocolId);
        buffer.writeShort(header.length);
        buffer.writeByte(header.unitId);

        return buffer;
    }

}
