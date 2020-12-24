package com.awy.common.modbus.server.context;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yhw
 */
@Slf4j
public final class SessionContext {

    private SessionContext(){}

    private static final AttributeKey<ModbusSession> SESSION = AttributeKey.newInstance("session");

    private static final Map<String, Channel> siteChannelMap = new ConcurrentHashMap<>();

    public static void bindSession(ModbusSession session, Channel channel) {
        if(channel != null && session != null){
            if(!hasLogin(channel)){
                String sessionId = ModbusSession.getSessionId(session.getManufacturer(), session.getEquipmentSerialNumber());
                siteChannelMap.put(sessionId,channel);
                channel.attr(SESSION).set(session);
                log.info("bind session : {} ....",session.toString());
            }
        }
    }

    public static void unBindSession(Channel channel) {
        if(hasLogin(channel)){
            ModbusSession session = getSession(channel);
            log.info("unbind session : {} ....",session.toString());
            siteChannelMap.remove(ModbusSession.getSessionId(session.getManufacturer(),session.getEquipmentSerialNumber()));
            channel.attr(SESSION).set(null);

        }
    }

    public static boolean hasLogin(Channel channel) {
        return channel.hasAttr(SESSION);
    }

    public static ModbusSession getSession(Channel channel) {
        return channel.attr(SESSION).get();
    }

    public static Channel getChannel(String sessionId) {
        return siteChannelMap.get(sessionId);
    }


    public static Map<String, Channel> getAllChannel(){
        return siteChannelMap;
    }

}
