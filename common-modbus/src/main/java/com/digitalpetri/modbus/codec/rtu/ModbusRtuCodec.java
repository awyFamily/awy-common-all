package com.digitalpetri.modbus.codec.rtu;

import com.digitalpetri.modbus.ModbusPdu;
import com.digitalpetri.modbus.UnsupportedPdu;
import com.digitalpetri.modbus.codec.ModbusPduDecoder;
import com.digitalpetri.modbus.codec.ModbusPduEncoder;
import com.digitalpetri.modbus.codec.utils.CRC;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;


/**
 * @author yhw
 */
@Slf4j
public class ModbusRtuCodec extends ByteToMessageCodec<ModbusRtuPayload> {

    private static final int packageLength = 6;

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
        //crc -此处需要(16位CRC校验 低字节在前)
        int readableBytes = buffer.readableBytes();//返回可读的字节数
        int startIndex = buffer.readerIndex();
        int end = startIndex + readableBytes;
        int[] crcArr = new int[end];
        for (int i = startIndex;i < end;i++){
            //数据是以补码的形式存储的，正数的补码是本身，负数的补码是除符号位外其它位取反，
            // 再加上1，1111 1111 是 补码，转换成原码是 1000 0001，就是 -1.
            //crc += buffer.getByte(i);
            //所以此处 获取无符号字节值返回
            crcArr[i] = buffer.getUnsignedByte(i);
        }
        String hexNumber = CRC.crc16(crcArr);
        buffer.writeShort(Integer.valueOf(hexNumber,16));
        //log
        printSendBufferHex(buffer);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> out) throws Exception {
        int endIndex = buffer.readableBytes();

        log.info("package startIndex : {} , readableBytes : {}",buffer.readerIndex(),buffer.readableBytes());
        //一个响应包最少是6位
        while (buffer.readableBytes() >= packageLength) {
            try {
                //log
                printReceiveBuffer(buffer);

                int siteId = buffer.readUnsignedByte();
                //控制类型
                ModbusPdu modbusPdu = decoder.decode(buffer);
                //跳出读取(指令错误)
                if (modbusPdu instanceof UnsupportedPdu) {
                    // Advance past any bytes we should have read but didn't...
                    //起始的 + 剩下的
//                    int endIndex = buffer.readerIndex() + buffer.readableBytes();
                    //设置可读取指标为 结束下标
                    buffer.readerIndex((buffer.readerIndex() + buffer.readableBytes()));
                    log.info("receive unsupported site : {} code : {}",siteId,modbusPdu.getFunctionCode());
                }else {
                    //get crc . receive buffer
                    log.info("current crc code : {}",Integer.toHexString(buffer.readUnsignedShort()));
                }
                out.add(new ModbusRtuPayload(siteId, modbusPdu));

            } catch (Throwable t) {
                throw new Exception("error decoding header/pdu", t);
            }
        }
    }

    /**
     * 测试输出的包内容
     * @param buffer
     */
    private void printReceiveBuffer(ByteBuf buffer){
        List<String> list = getBufferHex(buffer);
        log.info("receive package hex Array : {}",list.toString());
    }

    private void printSendBufferHex(ByteBuf buffer){
        List<String> list = getBufferHex(buffer);
        log.info("send package hex Array : {}",list.toString());
    }

    private List<String>  getBufferHex(ByteBuf buffer){
        int readableBytes = buffer.readableBytes();//返回可读的字节数
        int startIndex = buffer.readerIndex();
        List<String> list = new ArrayList<>();
        for (int i = startIndex;i < readableBytes;i++){
            list.add(Integer.toHexString(buffer.getUnsignedByte(i)));
        }
        return list;
    }


    /*@Override
    protected void encode(ChannelHandlerContext ctx, ModbusRtuPayload payload, ByteBuf buffer) {
//        buffer.writeZero(packageLength);
        buffer.writeByte(payload.getSiteId());
        encoder.encode(payload.getModbusPdu(), buffer);
        //crc -此处需要(16位CRC校验 低字节在前)
        int readableBytes = buffer.readableBytes();//返回可读的字节数
        int startIndex = buffer.readerIndex();
        int end = startIndex + readableBytes;
        int[] crcArr = new int[end];
        for (int i = startIndex;i < end;i++){
            //数据是以补码的形式存储的，正数的补码是本身，负数的补码是除符号位外其它位取反，
            // 再加上1，1111 1111 是 补码，转换成原码是 1000 0001，就是 -1.
            //crc += buffer.getByte(i);
            //所以此处 获取无符号字节值返回
            crcArr[i] = buffer.getUnsignedByte(i);
        }
        String hexNumber = CRC.crc16(crcArr);
        buffer.writeShort(Integer.valueOf(hexNumber,16));
    }*/

    /*@Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> out) throws Exception {
        //起始读取的下标
        int startIndex = buffer.readerIndex();

        //一个包最少是8位
        while (buffer.readableBytes() >= packageLength) {

            try {
                int siteId = buffer.readUnsignedByte();
                //控制类型
                ModbusPdu modbusPdu = decoder.decode(buffer);
                //跳出读取(指令错误)
                if (modbusPdu instanceof UnsupportedPdu) {
                    // Advance past any bytes we should have read but didn't...
                    int endIndex = startIndex + buffer.readableBytes();
                    //设置可读取指标为 结束下标
                    buffer.readerIndex(endIndex);
                    log.info("receive unsupported site : {} code : {}",siteId,modbusPdu.getFunctionCode());
                }
                out.add(new ModbusRtuPayload(siteId, modbusPdu));
            } catch (Throwable t) {
                throw new Exception("error decoding header/pdu", t);
            }

            //返回缓冲区可读的字节下标
            startIndex = buffer.readerIndex();
        }
    }*/

}
