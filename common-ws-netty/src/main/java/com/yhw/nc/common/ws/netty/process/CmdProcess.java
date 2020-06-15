package com.yhw.nc.common.ws.netty.process;

import com.yhw.nc.common.ws.netty.packets.Message;
import io.netty.channel.Channel;

/**
 * @author yhw
 */
public interface CmdProcess {

    /**
     * 消息接收处理
     * @param message 消息体
     * @param channel 上下文
     * @return 响应消息体(无响应,则返回null)
     */
    Message handler(Message message, Channel channel);

    /**
     * 设置命令码（此处的命令码，需要和消息包对应上）
     * @return 命令码
     */
    Byte getCmdCode();
}
