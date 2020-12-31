package com.awy.test.entity;

import com.awy.common.message.api.packets.Message;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class SimpleDataMessage<T> extends Message {


    private String sn;


    private String ver = "1";


    private List<String> toUserIds;


    private T data;

    @Override
    public Byte getCmd() {
        return 5;
    }
}
