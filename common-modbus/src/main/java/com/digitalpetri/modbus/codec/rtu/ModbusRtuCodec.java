package com.digitalpetri.modbus.codec.rtu;

import com.digitalpetri.modbus.ModbusPdu;
import com.digitalpetri.modbus.UnsupportedPdu;
import com.digitalpetri.modbus.codec.ModbusPduDecoder;
import com.digitalpetri.modbus.codec.ModbusPduEncoder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ModbusRtuCodec extends ByteToMessageCodec<ModbusRtuPayload> {

    private static final int packageLength = 8;

    private final ModbusPduEncoder encoder;
    private final ModbusPduDecoder decoder;

    public ModbusRtuCodec(ModbusPduEncoder encoder, ModbusPduDecoder decoder) {
        this.encoder = encoder;
        this.decoder = decoder;
    }

    //编码
    @Override
    protected void encode(ChannelHandlerContext ctx, ModbusRtuPayload payload, ByteBuf buffer) {
//        buffer.writeZero(packageLength);
        buffer.writeByte(payload.getSiteId());
        encoder.encode(payload.getModbusPdu(), buffer);
        //crc -此处需要
        int readableBytes = buffer.readableBytes();//返回可读的字节数
        int startIndex = buffer.readerIndex();
        int end = startIndex + readableBytes;
        int crc = 0;
        for (int i = startIndex;i < end;i++){
            crc += buffer.getByte(i);
        }
        buffer.writeShort(crc);
    }


    //解码
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> out) throws Exception {
        //起始读取的下标
        int startIndex = buffer.readerIndex();

        //一个包最少是8位
        while (buffer.readableBytes() >= packageLength) {

            try {
                int siteId = buffer.readByte();
                //控制类型
                ModbusPdu modbusPdu = decoder.decode(buffer);
                //跳出读取(指令错误)
                if (modbusPdu instanceof UnsupportedPdu) {
                    // Advance past any bytes we should have read but didn't...
                    int endIndex = startIndex + buffer.readableBytes();
                    //设置可读取指标为 结束下标
                    buffer.readerIndex(endIndex);
                }
                out.add(new ModbusRtuPayload(siteId, modbusPdu));
            } catch (Throwable t) {
                throw new Exception("error decoding header/pdu", t);
            }

            //返回缓冲区可读的字节下标
            startIndex = buffer.readerIndex();
        }
    }


}
