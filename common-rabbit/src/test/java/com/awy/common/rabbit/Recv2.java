package com.awy.common.rabbit;

import com.rabbitmq.client.*;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.support.DefaultMessagePropertiesConverter;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class Recv2 {

    private final static String QUEUE_NAME = "/yysj/load/report";
//    private final static String QUEUE_NAME = "/yysj/load/prod/control";
//    private final static String QUEUE_NAME = "testReport";

    public static void main(String[] argv) throws Exception {
        // 获取到连接以及mq通道
        Connection connection = ConnectionUtil.getConnection();
        Channel channel = connection.createChannel();

        // 声明队列
        ConnectionUtil.buildQueue(QUEUE_NAME,"",QUEUE_NAME,channel, BuiltinExchangeType.DIRECT);

        // 同一时刻服务器只会发一条消息给消费者
        channel.basicQos(1);

        DefaultMessagePropertiesConverter messagePropertiesConverter = new DefaultMessagePropertiesConverter();
        channel.basicConsume(QUEUE_NAME, false, "myConsumerTag",
                new DefaultConsumer(channel) {
                    @Override
                    public void handleDelivery(String consumerTag,
                                               Envelope envelope,
                                               AMQP.BasicProperties properties,
                                               byte[] body)
                            throws IOException
                    {
                      /*  try {
                            TimeUnit.SECONDS.sleep(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }*/

                        long deliveryTag = envelope.getDeliveryTag();
                        Message message = new Message(body,
                                messagePropertiesConverter.toMessageProperties(properties, envelope, "UTF-8"));
                        System.out.println(message.toString());
                        // (process the message components here ...)
                        channel.basicAck(deliveryTag, false);
                    }
                });
    }



}
