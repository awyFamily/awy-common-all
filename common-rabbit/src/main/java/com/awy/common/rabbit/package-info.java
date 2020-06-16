/**
 *
 *  1.消息持久化
 *  spring.jms.template.delivery-mode=2  #jms模式持久化消息
 *
 *  2.消息失败重试
 *  #启用消息发送成功确认，就是确认消息是否到达交换器中
 *  spring.rabbitmq.publisher-confirms=true
 *  #启用消息失败返回，比如路由不到队列时触发回调
 *  spring.rabbitmq.publisher-returns=true
 *
 *  3.消息消费确认
 *  open spring.rabbitmq.listener.simple.acknowledge-mode=manual
 */
package com.awy.common.rabbit;