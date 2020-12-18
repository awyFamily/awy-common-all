package com.awy.common.modbus.toolkit;

import com.awy.common.modbus.server.context.ModbusSession;
import com.awy.common.modbus.server.context.SessionContext;
import com.digitalpetri.modbus.codec.rtu.ModbusRtuPayload;
import com.digitalpetri.modbus.requests.ModbusRequest;
import io.netty.channel.Channel;

import java.util.List;

/**
 * @author yhw
 */
public final class SendUtil {

    /**
     * 发送消息
     * @param manufacturer 制造商编码
     * @param equipmentSerialNumber 设备序号
     * @param message 消息体
     */
    public static void sendRequest(int manufacturer,int equipmentSerialNumber, ModbusRequest message){
        String sessionId = ModbusSession.getSessionId(manufacturer, equipmentSerialNumber);
        sendRequest(sessionId,message);
    }

    public static void sendRequest(String sessionId, ModbusRequest message){
        Channel channel = SessionContext.getChannel(sessionId);
        if(channel != null && channel.isActive()){
            sendMsgChannel(new ModbusRtuPayload(SessionContext.getSession(channel).getSiteId(),message), SessionContext.getChannel(sessionId));
        }
    }

    public static void sendListRequest(List<String> sessionIds, ModbusRequest message){
        for (String sessionId : sessionIds) {
            sendRequest(sessionId,message);
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
