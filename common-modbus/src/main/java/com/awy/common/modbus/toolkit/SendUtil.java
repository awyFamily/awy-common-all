package com.awy.common.modbus.toolkit;

import com.awy.common.modbus.server.context.ModBusFutureContext;
import com.awy.common.modbus.server.context.ModbusSession;
import com.awy.common.modbus.server.context.SessionContext;
import com.digitalpetri.modbus.FunctionCode;
import com.digitalpetri.modbus.codec.rtu.ModbusRtuPayload;
import com.digitalpetri.modbus.requests.ModbusRequest;
import com.digitalpetri.modbus.responses.ModbusResponse;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author yhw
 */
@Slf4j
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

    /**
     * 发送消息并获取响应
     * @param manufacturer  制造商编码
     * @param equipmentSerialNumber 设备序号
     * @param message 消息体
     * @return
     */
    public static ModbusResponse sendRequestAwait(int manufacturer,int equipmentSerialNumber, ModbusRequest message){
        String sessionId = ModbusSession.getSessionId(manufacturer, equipmentSerialNumber);
        return sendRequestAwait(sessionId,message);
    }

    public static void sendRequest(String sessionId, ModbusRequest message){
        Channel channel = SessionContext.getChannel(sessionId);
        if(channel != null && channel.isActive()){
            sendMsgChannel(new ModbusRtuPayload(SessionContext.getSession(channel).getSiteId(),message), SessionContext.getChannel(sessionId));
        }
    }

    public static ModbusResponse sendRequestAwait(String sessionId, ModbusRequest message){
        Channel channel = SessionContext.getChannel(sessionId);
        if(channel != null && channel.isActive()){
            log.info("send sessionId : {} ",sessionId);
            String uid = ModBusFutureContext.getUid(sessionId,message.getFunctionCode());
            CompletableFuture<ModbusResponse> future = new CompletableFuture<>();
            ModBusFutureContext.put(uid,future);
            sendMsgChannel(new ModbusRtuPayload(SessionContext.getSession(channel).getSiteId(),message), SessionContext.getChannel(sessionId));
            try {
                return future.get(60, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                log.error("InterruptedException : ",e);
            } catch (ExecutionException e) {
                log.error("ExecutionException : ",e);
            } catch (TimeoutException e) {
                log.error("TimeoutException : ",e);
            }finally {
                ModBusFutureContext.removeFuture(uid);
            }
        }
        return null;
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
