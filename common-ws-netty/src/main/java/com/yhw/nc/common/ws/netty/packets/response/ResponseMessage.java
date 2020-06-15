package com.yhw.nc.common.ws.netty.packets.response;


import com.yhw.nc.common.ws.netty.constants.ImCommonConstant;
import com.yhw.nc.common.ws.netty.packets.Message;
import com.yhw.nc.common.ws.netty.toolkit.IdUtil;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 消息响应包
 * @author yhw
 */
@NoArgsConstructor
@Data
public class ResponseMessage extends Message {

    private String msg;

    @Override
    public Byte getCmd() {
        return ImCommonConstant.NOT_STATE_CODE;
    }

    public ResponseMessage(Integer code,String msg){
        super(code, IdUtil.randomId16());
        this.msg = msg;
    }

    public static ResponseMessage ok(String msg){
        return new ResponseMessage(ImCommonConstant.SUCCESS_CODE,msg);
    }

    public static ResponseMessage error(String msg){
        return new ResponseMessage(ImCommonConstant.ERROR_CODE,msg);
    }
}
