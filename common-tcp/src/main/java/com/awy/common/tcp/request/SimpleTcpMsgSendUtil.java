package com.awy.common.tcp.request;

import cn.hutool.core.util.StrUtil;
import com.awy.common.tcp.codec.BaseMessage;
import com.awy.common.tcp.codec.request.BaseRequest;
import com.awy.common.tcp.codec.response.BaseResponse;
import com.awy.common.tcp.context.SessionFactory;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author yhw
 * @date 2021-07-15
 */
@Slf4j
public final class SimpleTcpMsgSendUtil {

    private SimpleTcpMsgSendUtil(){}

    /**
     * send request
     * @param sessionId sessionId session
     * @param message message body
     */
    public static void sendRequest(String sessionId, BaseRequest message){
        if(StrUtil.isBlank(sessionId) || message == null){
            return;
        }
        Channel channel = SessionFactory.getChannel(sessionId);
        sendMsgChannel(message,channel);
    }


    public static BaseResponse sendRequestAwait(String sessionId, BaseRequest message){
        return sendRequestAwait(sessionId,message,30,TimeUnit.SECONDS);
    }

    public static BaseResponse sendRequestAwait(String sessionId, BaseRequest message,long timeout,TimeUnit timeUnit){
        Channel channel = SessionFactory.getChannel(sessionId);
        if(channel != null && channel.isActive()){
            log.info("send sessionId : {} ",sessionId);
            String uid = SimpleTcpFutureContext.getUid(sessionId,message.getMessageMark());

            CompletableFuture<BaseResponse> future = new CompletableFuture<>();
            SimpleTcpFutureContext.put(sessionId,future);
            boolean hasSend = sendMsgChannel(message, SessionFactory.getChannel(sessionId));
            try {
                if(hasSend){
                    return future.get(timeout, timeUnit);
                }
            } catch (InterruptedException e) {
                log.error("InterruptedException : ",e);
            } catch (ExecutionException e) {
                log.error("ExecutionException : ",e);
            } catch (TimeoutException e) {
                log.error("TimeoutException : ",e);
            }finally {
                SimpleTcpFutureContext.removeFuture(uid);
            }
        }
        return null;
    }

    private static boolean sendMsgChannel(BaseRequest message, Channel channel){
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
