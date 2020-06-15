package com.yhw.nc.common.rabbit.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@AllArgsConstructor
@Data
public class MessageWithTime {

    private long id;

    //current time
    private long time;

    private Object message;
}
