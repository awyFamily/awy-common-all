package com.awy.common.rabbit;


import com.awy.common.rabbit.model.DetailRes;

public interface MessageProcess<T> {
    /**
     * process message
     * @param message
     * @return  DetailRes
     */
    DetailRes process(T message);
}
