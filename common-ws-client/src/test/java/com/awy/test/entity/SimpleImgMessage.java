package com.awy.test.entity;

import com.awy.common.message.api.constants.MessageConstant;
import com.awy.common.message.api.packets.Message;
import com.awy.common.message.api.util.IdUtil;
import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@Data
public class SimpleImgMessage extends Message {

    private String imagesUrl;

    private String snNumber;


    public SimpleImgMessage(String imagesUrl, String snNumber){
        super(MessageConstant.SUCCESS_CODE, IdUtil.randomId16());
        this.imagesUrl = imagesUrl;
        this.snNumber = snNumber;
    }

    @Override
    public Byte getCmd() {
        return 3;
    }

}
