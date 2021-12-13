package com.awy.common.tcp.codec;

import com.awy.common.tcp.codec.response.BaseResponse;
import lombok.Data;
/**
 * @author yhw
 * @date 2021-07-14
 */
@Data
public class BaseMessage<T extends BaseResponse> {

    private T model;
}
