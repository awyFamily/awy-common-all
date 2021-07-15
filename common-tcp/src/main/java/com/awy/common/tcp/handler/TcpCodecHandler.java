package com.awy.common.tcp.handler;

import com.awy.common.tcp.codec.BaseMessage;
import com.awy.common.tcp.codec.ITcpDecoder;
import com.awy.common.tcp.codec.ITcpEncoder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
/**
 * @author yhw
 * @date 2021-07-14
 */
@Slf4j
public class TcpCodecHandler extends ByteToMessageCodec<BaseMessage> {

    private ITcpDecoder tcpDecoder;
    private ITcpEncoder tcpEncoder;

    public TcpCodecHandler(ITcpDecoder tcpDecoder, ITcpEncoder tcpEncoder){
        this.tcpDecoder = tcpDecoder;
        this.tcpEncoder = tcpEncoder;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, BaseMessage msg, ByteBuf out) throws Exception {
        tcpEncoder.encode(msg,out);
        printSendBufferHex(out);

    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        printReceiveBuffer(in);
//        BaseMessage message = tcpDecoder.decode(in);
        out.add(tcpDecoder.decode(in));

    }


    private void printReceiveBuffer(ByteBuf buffer){
        List<String> list = getBufferHex(buffer);
        log.info("receive package hex Array : {}",list.toString());
    }

    private void printSendBufferHex(ByteBuf buffer){
        List<String> list = getBufferHex(buffer);
        log.info("send package hex Array : {}",list.toString());
    }

    private List<String>  getBufferHex(ByteBuf buffer){
        int readableBytes = buffer.readableBytes();
        int startIndex = buffer.readerIndex();
        int endIndex = (startIndex + readableBytes);
        List<String> list = new ArrayList<>();
        for (int i = startIndex;i < endIndex;i++){
            list.add(Integer.toHexString(buffer.getUnsignedByte(i)));
        }
        return list;
    }
}
