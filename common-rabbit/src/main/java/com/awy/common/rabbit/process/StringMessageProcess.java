package com.awy.common.rabbit.process;

import com.awy.common.rabbit.MessageProcess;
import com.awy.common.rabbit.model.DetailRes;

public class StringMessageProcess implements MessageProcess<String> {

	@Override
	public DetailRes process(String message) {
		System.out.println(message);
		return new DetailRes(true, "",message);
	}
}
