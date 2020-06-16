package com.awy.common.rabbit;

import com.awy.common.rabbit.model.DetailRes;
import com.awy.common.rabbit.model.MessageWithTime;

/**
 * provider message
 */
public interface MessageSender {

	DetailRes send(Object message);

	/**
	 * timeout message
	 * @param messageWithTime
	 * @return
	 */
	DetailRes send(MessageWithTime messageWithTime);
}
