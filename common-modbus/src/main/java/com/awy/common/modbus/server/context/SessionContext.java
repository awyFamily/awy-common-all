package com.awy.common.modbus.server.context;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yhw
 */
public class SessionContext {

    private static final AttributeKey<Integer> SESSION = AttributeKey.newInstance("siteId");

    private static final Map<Integer, Channel> siteChannelMap = new ConcurrentHashMap<>();

    public static void bindSession(Integer siteId, Channel channel) {
        if(channel != null){
            siteChannelMap.put(siteId,channel);
            channel.attr(SESSION).set(siteId);
        }
    }

    public static void unBindSession(Channel channel) {
        if(hasLogin(channel)){
            siteChannelMap.remove(getSession(channel));
            channel.attr(SESSION).set(null);
        }
    }

    public static boolean hasLogin(Channel channel) {
        return channel.hasAttr(SESSION);
    }

    public static Integer getSession(Channel channel) {
        return channel.attr(SESSION).get();
    }

    public static Channel getChannel(Integer siteId) {
        return siteChannelMap.get(siteId);
    }


    public static Map<Integer, Channel> getAllChannel(){
        return siteChannelMap;
    }

}
