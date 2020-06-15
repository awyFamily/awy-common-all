package com.yhw.nc.common.rabbit;


import com.rabbitmq.client.*;
import com.yhw.nc.common.rabbit.model.DetailRes;
import com.yhw.nc.common.rabbit.model.MessageWithTime;
import com.yhw.nc.common.rabbit.model.RabbitConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.DefaultMessagePropertiesConverter;
import org.springframework.amqp.rabbit.support.MessagePropertiesConverter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * access builder
 */
public class MQAccessBuilder {

	private static Logger log = LoggerFactory.getLogger(MQAccessBuilder.class);

	private ConnectionFactory connectionFactory;

	public MQAccessBuilder(ConnectionFactory connectionFactory) {
		this.connectionFactory = connectionFactory;
	}

	/**
	 * builder provider
	 */
	public MessageSender buildDirectMessageSender(final String exchange, final String routingKey, final String queue) throws IOException {
		return buildMessageSender(exchange, routingKey, queue, BuiltinExchangeType.DIRECT);
	}

	public MessageSender buildTopicMessageSender(final String exchange, final String routingKey,String queue) throws IOException {
		return buildMessageSender(exchange, routingKey, queue, BuiltinExchangeType.TOPIC);
	}


	public MessageSender buildMessageSender(final String exchange, final String routingKey,
											final String queue, final BuiltinExchangeType type) throws IOException {
		Connection connection = connectionFactory.createConnection();
		Channel channel = connection.createChannel(false);
		switch (type){
			case DIRECT:
				buildQueue(exchange, routingKey, queue, channel, type);
				break;
			case TOPIC:
				exchangeDeclare(exchange,channel,type);
				break;
			default:
				break;
		}

		final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);

		/**
		 * Mandatory为true时,消息通过交换器无法匹配到队列会返回给生产者,为false时,匹配不到会直接被丢弃
		 */
		rabbitTemplate.setMandatory(true);
		rabbitTemplate.setExchange(exchange);
		rabbitTemplate.setRoutingKey(routingKey);
		//2 Serializable type
		rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
		RetryCache retryCache = new RetryCache();

		//3 sender confirm
		rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
			if (!ack) {
				log.info("send message failed: " + cause + correlationData.toString());
			} else {
				retryCache.del(Long.valueOf(correlationData.getId()));
			}
		});

		rabbitTemplate.setReturnCallback((message, replyCode, replyText, tmpExchange, tmpRoutingKey) -> {
			try {
				TimeUnit.MILLISECONDS.sleep(RabbitConstant.ONE_SECOND);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			log.info("send message failed: " + replyCode + " " + replyText);
			rabbitTemplate.send(message);
		});

		//4 builder sender
		return new MessageSender() {
			{
				retryCache.setSender(this);
			}

			@Override
			public DetailRes send(Object message) {
				long id = retryCache.generateId();
				long time = System.currentTimeMillis();

				return send(new MessageWithTime(id, time, message));
			}

			@Override
			public DetailRes send(MessageWithTime messageWithTime) {
				try {
					retryCache.add(messageWithTime);
					rabbitTemplate.correlationConvertAndSend(messageWithTime.getMessage(),
							new CorrelationData(String.valueOf(messageWithTime.getId())));
				} catch (Exception e) {
					return new DetailRes(false, e.getMessage());
				}

				return new DetailRes(true, "");
			}
		};
	}


	//==============================consumer=================================================

	public <T> MessageConsumer buildDirectMessageConsumer(String exchange, String routingKey, final String queue,
													final MessageProcess<T> messageProcess) throws IOException {
		return buildMessageConsumer(exchange, routingKey, queue, messageProcess, BuiltinExchangeType.DIRECT);
	}

	public <T> MessageConsumer buildTopicMessageConsumer(String exchange, String routingKey, final String queue,
														 final MessageProcess<T> messageProcess) throws IOException {
		return buildMessageConsumer(exchange, routingKey, queue, messageProcess, BuiltinExchangeType.TOPIC);
	}


	public <T> MessageConsumer buildMessageConsumer(String exchange, String routingKey, final String queue,
													final MessageProcess<T> messageProcess, BuiltinExchangeType type) throws IOException {
		final Connection connection = connectionFactory.createConnection();
		Channel channel = connection.createChannel(false);


		buildQueue(exchange, routingKey, queue, channel, type);
		//message conversion
		final MessagePropertiesConverter messagePropertiesConverter = new DefaultMessagePropertiesConverter();
		//Serializable type
		final MessageConverter messageConverter = new Jackson2JsonMessageConverter();

		return new MessageConsumer() {
			Channel channel;

			{
				channel = connection.createChannel(false);
			}

			@Override
			public DetailRes consume() {
				try {
					//1. get source data by basicGet
					GetResponse response = null;
					try{
						response = channel.basicGet(queue, false);
					}catch (Exception e){
						Thread.sleep(RabbitConstant.ONE_MINUTE);
						return new DetailRes(false, "process exception: " + e);
					}

					//poll . sleep if message isEmpty
					while (response == null) {
						response = channel.basicGet(queue, false);
						Thread.sleep(RabbitConstant.ONE_SECOND);
					}

					Message message = new Message(response.getBody(),
							messagePropertiesConverter.toMessageProperties(response.getProps(), response.getEnvelope(), "UTF-8"));

					//2 conversion assign model
					T messageBean = (T) messageConverter.fromMessage(message);

					//3
					DetailRes detailRes;

					try {
						//consumer message
						detailRes = messageProcess.process(messageBean);
					} catch (Exception e) {
						log.error("exception", e);
						detailRes = new DetailRes(false, "process exception: " + e);
					}

					//send Ack confirmation after success message process . send Nack retry after fail
					if (detailRes.isSuccess()) {
						channel.basicAck(response.getEnvelope().getDeliveryTag(), false);
					} else {
						//Avoid multiple requests provider log
						Thread.sleep(RabbitConstant.ONE_SECOND);
						log.info("process message failed: " + detailRes.getErrMsg());
						channel.basicNack(response.getEnvelope().getDeliveryTag(), false, true);
					}

					return detailRes;
				} catch (InterruptedException e) {
					log.error("exception", e);
					return new DetailRes(false, "interrupted exception " + e.toString());
				} catch (ShutdownSignalException | ConsumerCancelledException | IOException e) {
					log.error("exception", e);

					try {
						channel.close();
					} catch (IOException | TimeoutException ex) {
						log.error("exception", ex);
					}

					channel = connection.createChannel(false);

					return new DetailRes(false, "shutdown or cancelled exception " + e.toString());
				} catch (Exception e) {
					log.info("exception : ", e);

					try {
						channel.close();
					} catch (IOException | TimeoutException ex) {
						ex.printStackTrace();
					}

					channel = connection.createChannel(false);

					return new DetailRes(false, "exception " + e.toString());
				}
			}
		};
	}


	private void buildQueue(String exchange, String routingKey,
							final String queue, Channel channel, BuiltinExchangeType type) throws IOException {

		//声明交换器
		exchangeDeclare(exchange,channel,type);

		//exclusive (true -> 独占队列，当前连接关闭后，会删除队列, 此处false)
		channel.queueDeclare(queue, true, false, false, null);

		//队列绑定交换器
		channel.queueBind(queue, exchange, routingKey);

		try {
			channel.close();
		} catch (TimeoutException e) {
			log.info("close channel time out ", e);
		}
	}

	private void exchangeDeclare(String exchange, Channel channel,BuiltinExchangeType type) throws IOException {
		channel.exchangeDeclare(exchange, type.getType(), true, false, null);
	}


	public int getMessageCount(final String queue) throws IOException {
		Connection connection = connectionFactory.createConnection();
		final Channel channel = connection.createChannel(false);

		final AMQP.Queue.DeclareOk declareOk = channel.queueDeclarePassive(queue);

		return declareOk.getMessageCount();
	}
}
