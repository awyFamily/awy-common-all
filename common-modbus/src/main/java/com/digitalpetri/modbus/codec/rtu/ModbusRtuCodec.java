package com.digitalpetri.modbus.codec.rtu;

import com.digitalpetri.modbus.ModbusPdu;
import com.digitalpetri.modbus.UnsupportedPdu;
import com.digitalpetri.modbus.codec.ModbusPduDecoder;
import com.digitalpetri.modbus.codec.ModbusPduEncoder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;

import java.util.List;

public class ModbusRtuCodec extends ByteToMessageCodec<ModbusRtuPayload> {

    private static final int packageLength = 8;

    private final ModbusPduEncoder encoder;
    private final ModbusPduDecoder decoder;

    public ModbusRtuCodec(ModbusPduEncoder encoder, ModbusPduDecoder decoder) {
        this.encoder = encoder;
        this.decoder = decoder;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, ModbusRtuPayload payload, ByteBuf buffer) {
//        buffer.writeZero(packageLength);
        buffer.writeByte(payload.getSiteId());
        encoder.encode(payload.getModbusPdu(), buffer);
        //crc -此处需要(16位CRC校验 低字节在前)
        int readableBytes = buffer.readableBytes();//返回可读的字节数
        int startIndex = buffer.readerIndex();
        int end = startIndex + readableBytes;
        int crc = 0;
        for (int i = startIndex;i < end;i++){
            //数据是以补码的形式存储的，正数的补码是本身，负数的补码是除符号位外其它位取反，
            // 再加上1，1111 1111 是 补码，转换成原码是 1000 0001，就是 -1.
            //crc += buffer.getByte(i);
            //所以此处 获取无符号字节值返回
            crc += buffer.getUnsignedByte(i);
        }
        buffer.writeShort(crc);
    }


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
