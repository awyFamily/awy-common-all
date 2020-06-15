package com.yhw.nc.common.rabbit;

import com.yhw.nc.common.rabbit.model.DetailRes;
import com.yhw.nc.common.rabbit.model.MessageWithTime;

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
