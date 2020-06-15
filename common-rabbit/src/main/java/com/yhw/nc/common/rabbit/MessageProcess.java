package com.yhw.nc.common.rabbit;


import com.yhw.nc.common.rabbit.model.DetailRes;

public interface MessageProcess<T> {
    /**
     * process message
     * @param message
     * @return  DetailRes
     */
    DetailRes process(T message);
}
