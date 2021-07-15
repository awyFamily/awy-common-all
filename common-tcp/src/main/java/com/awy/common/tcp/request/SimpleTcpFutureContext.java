package com.awy.common.tcp.request;

import com.awy.common.tcp.codec.BaseMessage;
import com.awy.common.tcp.codec.response.BaseResponse;
import com.awy.common.tcp.context.BaseSession;
import com.awy.common.tcp.context.SessionFactory;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yhw
 * @date 2021-07-15
 */
@Slf4j
public final class SimpleTcpFutureContext {

    private SimpleTcpFutureContext(){}

    private static  final ConcurrentHashMap<String, CompletableFuture<BaseResponse>> waitMap = new ConcurrentHashMap<>();

    public static void put(String uid,CompletableFuture<BaseResponse> cf){
        waitMap.put(uid, cf);
    }

/*
    public static  BaseResponse getWaitHalfMinute(String uid){
        return getWait(uid,30,TimeUnit.SECONDS);
    }

    public static  BaseResponse getWait(String uid,long timeout,TimeUnit timeUnit){
        CompletableFuture<? extends BaseResponse> future = waitMap.get(uid);
        if(future == null){
            return null;
        }
        try {
            return future.get(timeout, timeUnit);
        } catch (InterruptedException e) {
            log.error("InterruptedException : ",e);
        } catch (ExecutionException e) {
            log.error("ExecutionException : ",e);
        } catch (TimeoutException e) {
            log.error("TimeoutException : ",e);
        }
        return null;
    }
*/


    public static void completeResponse(ChannelHandlerContext ctx, BaseMessage baseMessage){
        BaseSession session = SessionFactory.getSession(ctx.channel());
        if(session == null){
            log.error("session is empty...");
            return;
        }

        String uid = getUid(session.getSessionId(), baseMessage.getModel().getMessageMark());
        completeFuture(uid,baseMessage.getModel());
        /*CompletableFuture.runAsync(() -> {
            String sessionId = ModbusSession.getSessionId(session.getManufacturer(), session.getEquipmentSerialNumber());
            String uid = ModBusFutureContext.getUid(sessionId, payload.getModbusPdu().getFunctionCode());
            ModBusFutureContext.completeFuture(uid,(ModbusResponse)payload.getModbusPdu());
        });*/
    }

    private static void completeFuture(String uid, BaseResponse response){
        CompletableFuture<BaseResponse> future = waitMap.get(uid);
        if(future == null){
            return;
        }
        future.complete(response);
    }

    public static void removeFuture(String uid){
        waitMap.remove(uid);
    }

    public static String getUid(String sessionId, String messageMark){
        return sessionId.concat(":").concat(messageMark);
    }

}
