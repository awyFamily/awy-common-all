package com.awy.common.ws.netty.cluster;

import com.awy.common.ws.netty.model.ClusterMessage;

/**
 * 多节点通过 发布订阅(广播)进行消息通知
 * @author yhw
 */
public interface ImClusterTopic {

    /**
     * 发布消息
     */
    void publish(ClusterMessage message);

    /**
     * 订阅消息(采用广播模式)
     */
    void consumer();
}
