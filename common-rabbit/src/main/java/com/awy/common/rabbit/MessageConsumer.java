package com.awy.common.rabbit;


import com.awy.common.rabbit.model.DetailRes;

public interface MessageConsumer {
    DetailRes consume();
}
