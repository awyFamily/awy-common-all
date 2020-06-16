package com.awy.common.message.api.packets.response;


import com.awy.common.message.api.packets.Message;
import com.awy.common.message.api.constants.MessageConstant;
import com.awy.common.message.api.util.IdUtil;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 消息响应包
 */
@NoArgsConstructor
@Data
public class ResponseMessage extends Message {

    private String msg;

    @Override
    public Byte getCmd() {
        return MessageConstant.NOT_STATE_CODE;
    }

    public ResponseMessage(Integer code, String msg){
        super(code, IdUtil.randomId16());
        this.msg = msg;
    }

    public static ResponseMessage ok(String msg){
        return new ResponseMessage(MessageConstant.SUCCESS_CODE,msg);
    }

    public static ResponseMessage error(String msg){
        return new ResponseMessage(MessageConstant.ERROR_CODE,msg);
    }
}
