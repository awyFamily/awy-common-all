package com.awy.common.modbus.toolkit;

import com.awy.common.modbus.server.context.SessionContext;
import com.digitalpetri.modbus.codec.rtu.ModbusRtuPayload;
import com.digitalpetri.modbus.requests.ModbusRequest;
import io.netty.channel.Channel;
import io.netty.util.ReferenceCountUtil;

import java.util.List;

public class SendUtil {

    public static void sendRequest(Integer siteId, ModbusRequest message){
        sendMsgChannel(new ModbusRtuPayload(siteId,message), SessionContext.getChannel(siteId));
    }

    public static void sendListRequest(List<Integer> siteIds, ModbusRequest message){
        for (Integer siteId : siteIds) {
            sendMsgChannel(new ModbusRtuPayload(siteId,message), SessionContext.getChannel(siteId));
        }
    }

    private static boolean sendMsgChannel(ModbusRtuPayload message, Channel channel){
        if(message != null && channel != null){
            if(channel.isActive()){
                channel.writeAndFlush(message);
//                ReferenceCountUtil.release(message);
                return true;
            }
        }
        return false;
    }
}
