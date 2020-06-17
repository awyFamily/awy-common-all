/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.awy.common.ws.netty.server.handler;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.awy.common.message.api.packets.Message;
import com.awy.common.message.api.packets.response.ResponseMessage;
import com.awy.common.ws.netty.context.MessageManager;
import com.awy.common.ws.netty.context.ProcessManager;
import com.awy.common.ws.netty.process.CmdProcess;
import com.awy.common.ws.netty.toolkit.ImSendUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

/**
 * webSocket 长链接 处理
 * @author yhw
 */
@ChannelHandler.Sharable
public class WebSocketFrameHandler extends SimpleChannelInboundHandler<WebSocketFrame> {


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame frame) throws Exception {
        // ping and pong frames already handled
        Message response;
        if (frame instanceof TextWebSocketFrame) {
            // Send the uppercase string back.
            String request = ((TextWebSocketFrame) frame).text();
//            System.err.println("收到文本消息>>>>>>>>>>>>>>>>>>>>>>>>>>" + request);
//            ctx.channel().writeAndFlush(new TextWebSocketFrame(request.toUpperCase(Locale.US)));
            JSONObject requestObj;
            try {
                requestObj = JSONUtil.parseObj(request);
                Byte code = requestObj.getByte("cmd");
                if(code != null){
                    Class<? extends Message> clazz = MessageManager.getInstance().getMessage(code);

                    Message message = JSONUtil.toBean(requestObj, clazz);

                    CmdProcess process = ProcessManager.getInstance().getCmdProcess(code);

                    if(process != null){
                        response = process.handler(message,ctx.channel());
                    }else {
                        response = ResponseMessage.error("消息编码不存在");
                    }
                }else {
                    response = ResponseMessage.error("请传递合法的指令");
                }

            } catch (Exception e) {
                e.printStackTrace();
                response = ResponseMessage.error("请传递合法的消息体");
            }
            if(response != null){
                //做回应，服务器是否处理成功
                ctx.channel().writeAndFlush(ImSendUtil.getMessage(response));
            }
        } else {
            String message = "unsupported frame type: " + frame.getClass().getName();
            throw new UnsupportedOperationException(message);
        }
    }


    //此处需要拓展（策略 strategy ）
    //1.查询用户是否在线
    //2 在线
    //2.1 当前用户是否属于  当前节点的的channel，则直接进行推送
    //2.2 如果是其他节点，则推送到消息队列MQ（广播传播方式） ，直接通过ImKit 进行推送
    //3.离线
    //3.1 对消息进行存储，用户上线进行消息推送(群组通过最后读取消息ID进行判断是否已读，避免用户存储多份同样的消息)
}
