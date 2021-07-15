package com.awy.common.tcp.context;

import com.awy.common.tcp.codec.BaseMessage;
import io.netty.channel.Channel;
/**
 * @author yhw
 * @date 2021-07-14
 */
public interface ISessionLifecycle<T extends BaseSession> {

    T createSession(BaseMessage message);

    void bind(Channel channel,T session);

    void onHeartbeat(T session);

    void unbind(T session);


}
