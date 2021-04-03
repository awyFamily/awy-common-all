package com.awy.common.rabbit;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.MessageProperties;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class WorkQueueDemo {

    private final static String QUEUE_NAME = "testReport";

    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        Connection connection = ConnectionUtil.getConnection();
        Channel channel = connection.createChannel();
        channel.queueDeclare(QUEUE_NAME, true, false, false, null);
        // 声明队列
//        ConnectionUtil.buildQueue(QUEUE_NAME,"",QUEUE_NAME,channel, BuiltinExchangeType.DIRECT);
        Thread.sleep(1000);
        for (int i = 0; i < 10; i++) {
            channel.basicPublish(QUEUE_NAME, "", MessageProperties.PERSISTENT_TEXT_PLAIN, ("生产者" + i).getBytes());
//            System.out.println("生产者" + i);
        }
        ConnectionUtil.closeConnectionAndChannel(channel,connection);
    }

}
