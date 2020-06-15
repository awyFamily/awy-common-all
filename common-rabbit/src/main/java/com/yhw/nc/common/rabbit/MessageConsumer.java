package com.yhw.nc.common.rabbit;


import com.yhw.nc.common.rabbit.model.DetailRes;

public interface MessageConsumer {
    DetailRes consume();
}
