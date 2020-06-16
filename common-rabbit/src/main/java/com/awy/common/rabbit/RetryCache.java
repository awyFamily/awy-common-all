package com.awy.common.rabbit;


import com.awy.common.rabbit.model.DetailRes;
import com.awy.common.rabbit.model.MessageWithTime;
import com.awy.common.rabbit.model.RabbitConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicLong;


public class RetryCache {
	private static Logger log = LoggerFactory.getLogger(RetryCache.class);
    private MessageSender sender;
    private boolean stop = false;
    private Map<Long, MessageWithTime> map = new ConcurrentSkipListMap<>();
    private AtomicLong id = new AtomicLong();

    public void setSender(MessageSender sender) {
        this.sender = sender;
        startRetry();
    }

    public long generateId() {
        return id.incrementAndGet();
    }

    public void add(MessageWithTime messageWithTime) {
        map.putIfAbsent(messageWithTime.getId(), messageWithTime);
    }

    public void del(long id) {
        map.remove(id);
    }

    private void startRetry() {
        new Thread(() ->{
            while (!stop) {
                try {
                    Thread.sleep(RabbitConstant.RETRY_TIME_INTERVAL);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                long now = System.currentTimeMillis();

                for (Map.Entry<Long, MessageWithTime> entry : map.entrySet()) {
                    MessageWithTime messageWithTime = entry.getValue();

                    if (null != messageWithTime) {
                        //message retry 3 min then give up message
                        if (messageWithTime.getTime() + (3 * RabbitConstant.VALID_TIME) < now) {
                            log.info("send message {} failed after 3 min ", messageWithTime);
                            del(entry.getKey());
                        } else if (messageWithTime.getTime() + RabbitConstant.VALID_TIME < now) {
                            DetailRes res = sender.send(messageWithTime);

                            if (!res.isSuccess()) {
                                log.info("retry send message failed {} errMsg {}", messageWithTime, res.getErrMsg());
                            }
                        }
                    }
                }
            }
        }).start();
    }

}
