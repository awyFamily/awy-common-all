package com.awy.common.tcp.context;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yhw
 * @date 2021-07-14
 */
@Slf4j
public class SessionFactory {

    private static  final AttributeKey<BaseSession> SESSION  = AttributeKey.newInstance("session");

    private static  final Map<String, Channel> siteChannelMap = new ConcurrentHashMap<>();

    private SessionFactory(){
    }


    public static void bindSession(BaseSession session, Channel channel) {
        if(channel != null && session != null){
            if(!hasLogin(channel)){
                siteChannelMap.put(session.getSessionId(),channel);
                channel.attr(SESSION).set(session);
                log.info("bind session : {} ....",session.toString());
            }
        }
    }

    public static void unBindSession(Channel channel) {
        if(hasLogin(channel)){
            BaseSession session = getSession(channel);
            log.info("unbind session : {} ....",session.toString());
            siteChannelMap.remove(session.getSessionId());
            channel.attr(SESSION).set(null);

        }
    }

    public static boolean hasLogin(Channel channel) {
        return channel.hasAttr(SESSION);
    }

    public static BaseSession getSession(Channel channel) {
        return channel.attr(SESSION).get();
    }

    public static Channel getChannel(String sessionId) {
        return siteChannelMap.get(sessionId);
    }


    public static Map<String, Channel> getAllChannel(){
        return siteChannelMap;
    }
}
