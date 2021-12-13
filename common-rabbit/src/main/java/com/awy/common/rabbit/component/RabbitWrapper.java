package com.awy.common.rabbit.component;

import com.awy.common.rabbit.MessageProcess;
import com.awy.common.rabbit.model.DetailRes;
import com.awy.common.rabbit.model.RabbitConstant;
import com.awy.common.rabbit.process.StringMessageProcess;
import com.rabbitmq.client.BuiltinExchangeType;
import com.awy.common.rabbit.MQAccessBuilder;
import com.awy.common.rabbit.MessageConsumer;
import com.awy.common.rabbit.MessageSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @author yhw
 */
@Component
public class RabbitWrapper {

	private static Logger logger = LoggerFactory.getLogger(RabbitWrapper.class);

	@Resource
	private RabbitAdmin rabbitAdmin;
	@Resource
	private RabbitTemplate rabbitTemplate;
	@Resource
	private ConnectionFactory connectionFactory;

	//========================== provider ==========================

	/**
	 * 使用默认路由密钥将消息发送到默认交换机
	 * @param msg
	 */
	public void send(String msg){
		rabbitTemplate.send(getDefaultMessage(msg));
	}

	/**
	 * 使用特定路由密钥将消息发送到默认交换机。
	 * @param routingKey 路由
	 * @param msg 消息
	 */
	public void send(String routingKey,String msg){
		rabbitTemplate.send(routingKey,getDefaultMessage(msg));
	}

	/**
	 * 发送消息
	 * 使用特定路由密钥将消息发送到特定交换机
	 * @param exchange 交换器名称
	 * @param routingKey 路由key
	 * @param msg 消息体
	 */
	public void send(String exchange,String routingKey,String msg){
		rabbitTemplate.send(exchange,routingKey,getDefaultMessage(msg));
	}

	/**
	 * 使用特定路由密钥将消息发送到特定交换机。
	 * @param exchange 交换器名称
	 * @param routingKey 路由key
	 * @param msg 消息体
	 * @param correlationData
	 */
	public void send(String exchange,String routingKey,String msg,CorrelationData correlationData){
		rabbitTemplate.send(exchange,routingKey,getDefaultMessage(msg),correlationData);
	}

	//====================== 频繁发布消息 不建议使用增强方法，损耗IO, 订阅 getMessageSender 方法,发送消息===============================


	/**
	 * 发送消息Topic类型交换器增强(发送失败会自动重试)
	 * @param exchange 交换机名称
	 * @param message 消息体
	 * @return
	 */
	public DetailRes sendTopicPlus(String exchange, String queue, Object message){
		return sendTopicPlus(exchange,"",queue,message);
	}

	/**
	 * 发送消息Topic类型交换器增强(不建议直接调用,
	 * 发送失败会自动重试)
	 * 建议直接使用：getMessageSender 获取消息发送对象
	 * @param exchange 交换机名称
	 * @param routing 路由key
	 * @param message 消息
	 * @return 消息响应
	 */
	public DetailRes sendTopicPlus(String exchange,String routing,String queue,Object message){
		MessageSender messageSender = getMessageSender(exchange,routing,queue, BuiltinExchangeType.TOPIC);
		return messageSender.send(message);
	}

	/**
	 * 发送消息Direct(默认)类型交换器增强
	 * @param exchange
	 * @param queue
	 * @param message
	 * @return
	 */
	public DetailRes sendDirectPlus(String exchange,String queue, Object message){
		return sendDirectPlus(exchange,"",queue,message);
	}

	/**
	 * 发送重试消息(fail to retry sending)
	 * 不建议直接使用
	 * 建议直接使用：getMessageSender 获取消息发送对象
	 * @param exchange 交换机
	 * @param routing 路由key
	 * @param queue 队列名称
	 * @param message 消息体
	 * @return 发送消息状态
	 */
	public DetailRes sendDirectPlus(String exchange,String routing,String queue, Object message){
		MessageSender messageSender = getMessageSender(exchange,routing,queue,BuiltinExchangeType.DIRECT);
		return messageSender.send(message);
	}

	//=================================== consumer 需要一直消费消息不建议使用增强方法,会频繁的建立连接,增加IO消耗 ================================

	/**
	 * 消费默认队列消息
	 */
	public void receiveDefaultQueue(){
		rabbitTemplate.receive();
	}

	/**
	 * 消费指定队列消息
	 * @param queueName
	 */
	public String receive(final String queueName){
		Message message = rabbitTemplate.receive(queueName);
		if(message == null){
			return "";
		}
		return new String(message.getBody());
	}

	/**
	 * 增强消费Topic交换器(使用所有增强消费,会一直阻塞 直到获取到消息才会返回)
	 *  频繁获取消息,建议使用：getMessageConsumer
	 */
	@Deprecated
	public String receiveTopicPlus(final String exchange,final String queue){
		return receiveTopicPlus(exchange,"",queue);
	}

	/**
	 * 增强消费Topic交换机(不建议使用)
	 * 频繁获取消息,建议使用：getMessageConsumer
	 */
	@Deprecated
	public String receiveTopicPlus(final String exchange,final String routing,final String queue){
		return this.receivePlus(exchange,routing,queue,BuiltinExchangeType.TOPIC);
	}

	/**
	 * Direct消费增强(不建议使用)
	 * 频繁获取消息,建议使用：getMessageConsumer
	 */
	@Deprecated
	public String receiveDirectPlus(final String exchange,final String queue){
		return receiveDirectPlus(exchange,"",queue);
	}

	/**
	 * Direct消费增强(不建议使用)
	 * 频繁获取消息,建议使用：getMessageConsumer
	 */
	@Deprecated
	public String receiveDirectPlus(final String exchange,final String routing,final String queue){
		return receivePlus(exchange,routing,queue,BuiltinExchangeType.DIRECT);
	}

	/**
	 * 消费增强(不推荐使用)
	 * 频繁获取消息,建议使用：getMessageConsumer
	 */
	@Deprecated
	private String receivePlus(final String exchange, final String routing, final String queue,BuiltinExchangeType typeEnum){
		MessageConsumer messageConsumer = getMessageConsumer(exchange,routing,queue,new StringMessageProcess(),typeEnum);
		DetailRes result = messageConsumer.consume();
		if(!result.isSuccess()){
			logger.error("receive TopicPlus error >>>>.".concat(result.getErrMsg()));
			return "";
		}
		return result.getBody().toString();
	}

	/**
	 * 定制消费增强(不建议使用)
	 * 频繁获取消息,建议使用：getMessageConsumer
	 * @param exchange 交换机名称
	 * @param queue 队列名称
	 * @param typeEnum 交换机类型
	 * @param messageProcess 实现回调接口
	 * @param <T> 回调返回结果类型
	 * @return DetailRes
	 */
	public <T> DetailRes receiveCustomizePlus(final String exchange, final String queue,BuiltinExchangeType typeEnum,final MessageProcess<T> messageProcess){
		return CustomizePlus(exchange,"",queue,typeEnum,messageProcess);
	}

	/**
	 * 定制消费增强(不建议使用)
	 * 频繁获取消息,建议使用：getMessageConsumer
	 * @param exchange 交换机名称
	 * @param routing 路由key
	 * @param queue 队列名称
	 * @param typeEnum 交换机类型
	 * @param messageProcess 实现回调接口
	 * @return DetailRes
	 */
	public <T> DetailRes CustomizePlus(final String exchange, final String routing, final String queue,BuiltinExchangeType typeEnum,final MessageProcess<T> messageProcess){
		MessageConsumer messageConsumer = getMessageConsumer(exchange,routing,queue,messageProcess,typeEnum);
		return messageConsumer.consume();
	}




	//============================== admin ============================================

	public void createExchangeAndQueueBind(String exchangeName,BuiltinExchangeType type,String queueName,String routeKey){
		Exchange exchange = createExchange(exchangeName, type);
		if(exchange != null){
			Queue queue = createQueue(queueName);
			bind(queue,exchange,routeKey);
		}
	}

	public Exchange createExchange(String exchangeName,BuiltinExchangeType type){
		//交换器名称，durable(服务器重启后继续存在)，自动删除(服务器不使用时自动删除交换器)
		//默认创建持久存在，不自动删除的交换器
		Exchange exchange;
		switch (type){
			case DIRECT:
				exchange = new DirectExchange(exchangeName);
				break;
			case TOPIC:
				exchange = new TopicExchange(exchangeName);
				break;
			case FANOUT:
				exchange = new FanoutExchange(exchangeName);
				break;
			case HEADERS:
				exchange = new HeadersExchange(exchangeName);
				break;
			default:
				exchange = new DirectExchange(exchangeName);
				break;
		}
		try{
			rabbitAdmin.declareExchange(exchange);
		}catch (Exception e){
			logger.error("createTopicExchange error",e);
		}
		return exchange;
	}

	public Queue createQueue(String queueName){
		//队列是持久的，非独占的和非自动删除
		//exclusive - 如果我们声明一个独占队列，则为true（该队列仅由声明者的连接使用）
		Queue queue = new Queue(queueName);
		rabbitAdmin.declareQueue(queue);
		return queue;
	}

	/**
	 * 队列交换机绑定
	 * @param queue 队列
	 * @param exchange 交换机
	 * @param routingKey 路由 topic.#(多级通配) topic.*(一级通配) topic.message(固定路由)
	 */
	public void bind(Queue queue,Exchange exchange,String routingKey){
		Binding binding = BindingBuilder
				.bind(queue)
				.to(exchange)
				.with(routingKey)
				.noargs();
		rabbitAdmin.declareBinding(binding);
	}

	/**
	 * 删除队列，不管它是否正在使用或是否有消息
	 *  queueName - 队列的名称。
	 *  unused - 如果仅在未使用时才删除队列，则为true。
	 *  empty - 如果仅在空的情况下才应删除队列，则为true。
	 * @param queueName
	 */
	public void deleteQueue(String queueName){
		rabbitAdmin.deleteQueue(queueName);
	}

	/**
	 * 删除交换器
	 * @param exchangeName 交换器名称
	 */
	public void deleteExchange(String exchangeName){
		rabbitAdmin.deleteExchange(exchangeName);
	}

	//========================== other help method =================================================


	private Message getDefaultMessage(String msg){
		MessageProperties messageProperties = new MessageProperties();
		messageProperties.setContentType(MessageProperties.CONTENT_TYPE_JSON);
		return new Message(msg.getBytes(),messageProperties);
	}

	/**
	 * get MessageSender
	 * 提供者增强
	 */
	public MessageSender getMessageSender(String exchange, String routing, String queue, BuiltinExchangeType typeEnum){
		try{
			MessageSender messageSender = null;
			MQAccessBuilder mqAccessBuilder = new MQAccessBuilder(connectionFactory);
			switch (typeEnum){
				case DIRECT:
					messageSender = mqAccessBuilder.buildDirectMessageSender(exchange, routing, queue);
					break;
				case TOPIC:
					messageSender = mqAccessBuilder.buildTopicMessageSender(exchange,routing,queue);
					break;
			}
			return messageSender;
		}catch (IOException e){
			logger.error("getMessageSender IO error ",e);
		}
		return null;
	}

	/**
	 * get MessageConsumer
	 * 消费者增强
	 */
	public  <T> MessageConsumer getMessageConsumer(String exchange, String routing, String queue, final MessageProcess<T> messageProcess, BuiltinExchangeType typeEnum){
		try{
			MQAccessBuilder mqAccessBuilder = new MQAccessBuilder(connectionFactory);
			return mqAccessBuilder.buildMessageConsumer(exchange, routing, queue, messageProcess, typeEnum);
		}catch (IOException e){
			logger.error("getMessageSender IO error ",e);
		}
		return null;
	}


	/**
	 * 通过通道名称获取队列名称
	 * @param name
	 * @return 返回默认队列名称
	 */
	public String getDefaultQueueName(String name){
		return RabbitConstant.DEFAULT_EXCHANGE.concat(".").concat(name);
	}
}
