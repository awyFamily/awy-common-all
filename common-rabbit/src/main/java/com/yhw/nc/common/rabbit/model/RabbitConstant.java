package com.yhw.nc.common.rabbit.model;


public interface RabbitConstant {

	//线程数
	int THREAD_COUNT = 5;

	//处理间隔时间 mils
	int INTERVAL_MILS = 0;

	//consumer失败后等待时间(mils)
	int ONE_SECOND = 1 * 1000;

	//异常sleep时间(mils)
	int ONE_MINUTE = 1 * 60 * 1000;

	//MQ消息retry时间
	int RETRY_TIME_INTERVAL = ONE_MINUTE;

	//MQ消息有效时间
	int VALID_TIME = ONE_MINUTE;

	//默认交换机名称
	String DEFAULT_EXCHANGE = "cloud-monitor";

}
