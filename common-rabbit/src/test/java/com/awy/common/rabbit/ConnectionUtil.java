package com.awy.common.rabbit;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;


public class ConnectionUtil {

    public static Connection getConnection() throws IOException, TimeoutException {
        //连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        //获取连接
        factory.setHost("123");
        //连接5672端口  注意15672为工具界面端口  25672为集群端口
        factory.setPort(5672);
//        factory.setVirtualHost("/");
         factory.setUsername("123");
         factory.setPassword("123");
        //获取连接
        Connection connection = factory.newConnection();
        return connection;
    }

    public static void closeConnectionAndChannel(Channel channel, Connection connection) {
        try {
            if(channel!=null){
                channel.close();
            }
            if(connection!=null){
                connection.close();
            }
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    public static void buildQueue(String exchange, String routingKey,
                            final String queue, Channel channel, BuiltinExchangeType type) throws IOException {

        //声明交换器
        exchangeDeclare(exchange,channel,type);

        //exclusive (true -> 独占队列，当前连接关闭后，会删除队列, 此处false)
        channel.queueDeclare(queue, true, false, false, null);

        //队列绑定交换器
        channel.queueBind(queue, exchange, routingKey);

//        try {
//            channel.close();
//        } catch (TimeoutException e) {
//            System.out.println("close channel time out ");
//        }
    }

    private static void exchangeDeclare(String exchange, Channel channel,BuiltinExchangeType type) throws IOException {
        channel.exchangeDeclare(exchange, type.getType(), true, false, null);
    }
}
