package com.awy.common.modbus.server.context;

import com.digitalpetri.modbus.FunctionCode;
import com.digitalpetri.modbus.codec.rtu.ModbusRtuPayload;
import com.digitalpetri.modbus.responses.ModbusResponse;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

/**
 * @author yhw
 */
@Slf4j
public final class ModBusFutureContext {

    private ModBusFutureContext(){}

    private static  final ConcurrentHashMap<String, CompletableFuture<ModbusResponse>> waitMap = new ConcurrentHashMap<>();

    public static void put(String uid,CompletableFuture<ModbusResponse> cf){
        waitMap.put(uid, cf);
    }

    public static  ModbusResponse getWaitOneMinute(String uid){
        CompletableFuture<? extends ModbusResponse> future = waitMap.get(uid);
        if(future == null){
            return null;
        }
        try {
            return future.get(60, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.error("InterruptedException : ",e);
        } catch (ExecutionException e) {
            log.error("ExecutionException : ",e);
        } catch (TimeoutException e) {
            log.error("TimeoutException : ",e);
        }
        return null;
    }


    public static void completeResponse(ChannelHandlerContext ctx, ModbusRtuPayload payload){
        ModbusSession session = SessionContext.getSession(ctx.channel());
        if(session == null){
            return;
        }
        String sessionId = ModbusSession.getSessionId(session.getManufacturer(), session.getEquipmentSerialNumber());
        String uid = ModBusFutureContext.getUid(sessionId, payload.getModbusPdu().getFunctionCode());
        ModBusFutureContext.completeFuture(uid,(ModbusResponse)payload.getModbusPdu());
    }

    public static void completeFuture(String uid,ModbusResponse response){
        CompletableFuture<ModbusResponse> future = waitMap.get(uid);
        if(future == null){
            return;
        }
        future.complete(response);
    }

    public static void removeFuture(String uid){
        waitMap.remove(uid);
    }

    public static String getUid(String sessionId, FunctionCode functionCode){
        return sessionId.concat(":").concat(String.valueOf(functionCode.getCode()));
    }

}
