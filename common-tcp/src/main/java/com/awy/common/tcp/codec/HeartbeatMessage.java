package com.awy.common.tcp.codec;

import com.awy.common.tcp.codec.response.SimpleResponse;
import lombok.Data;

/**
 * @author yhw
 * @date 2021-07-15
 */
@Data
public class HeartbeatMessage extends BaseMessage<SimpleResponse> {
}
