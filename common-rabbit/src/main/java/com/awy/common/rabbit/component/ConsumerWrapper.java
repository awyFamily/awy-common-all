package com.awy.common.rabbit.component;

import cn.hutool.core.lang.Assert;
import com.awy.common.rabbit.MessageConsumer;
import com.awy.common.rabbit.MessageProcess;
import com.awy.common.rabbit.model.DetailRes;
import com.awy.common.rabbit.process.StringMessageProcess;
import com.rabbitmq.client.BuiltinExchangeType;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * @author yhw
 */
@Slf4j
public class ConsumerWrapper {

    public ConsumerWrapper(RabbitWrapper rabbitWrapper){
        this.rabbitWrapper = rabbitWrapper;
    }

    private final RabbitWrapper rabbitWrapper;

    private Thread consumerThread;

    private volatile boolean consumerThreadStop = false;

    public void consumerDirect(String exchange,String queue, Consumer<String> consumer){
        this.consumer(exchange,"",queue,consumer,new StringMessageProcess(),BuiltinExchangeType.DIRECT);
    }

    public void consumerDirect(String exchange,String routingKey,String queue, Consumer<String> consumer){
        this.consumer(exchange,routingKey,queue,consumer,new StringMessageProcess(),BuiltinExchangeType.DIRECT);
    }

    public void consumerTopic(String exchange, String queue, Consumer<String> consumer){
        this.consumer(exchange,"",queue,consumer,new StringMessageProcess(),BuiltinExchangeType.TOPIC);
    }
    public void consumerTopic(String exchange,String routingKey, String queue, Consumer<String> consumer){
        this.consumer(exchange,routingKey,queue,consumer,new StringMessageProcess(),BuiltinExchangeType.TOPIC);
    }

    public <T> void consumer(String exchange, String routingKey, String queue, Consumer<String> consumer, final MessageProcess<T> messageProcess, BuiltinExchangeType typeEnum){
        Assert.isFalse(consumerThread != null,"consumer is running");
        String threadName = typeEnum.getType();
        consumerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                MessageConsumer messageConsumer = rabbitWrapper.getMessageConsumer(exchange, routingKey, queue,messageProcess,typeEnum);
                while (!consumerThreadStop){
                    DetailRes result = messageConsumer.consume();
                    if(!result.isSuccess()){
                        log.error("receive message error >>>>.".concat(result.getErrMsg()));
                    }else {
                        consumer.accept(result.getBody().toString());
                    }
                }
            }
        },"awy-" + threadName + "-consumer-thread");
        consumerThread.setDaemon(true);
        consumerThread.start();
    }

    public void destroy() {
        if(consumerThread == null){
            return;
        }
        this.consumerThreadStop = true;
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }
        if (consumerThread.getState() != Thread.State.TERMINATED){
            // interrupt and wait
            consumerThread.interrupt();
            try {
                consumerThread.join();
            } catch (InterruptedException e) {
                log.error(e.getMessage(), e);
            }
        }
    }

}
