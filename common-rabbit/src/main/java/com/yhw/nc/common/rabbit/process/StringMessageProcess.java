package com.yhw.nc.common.rabbit.process;

import com.yhw.nc.common.rabbit.MessageProcess;
import com.yhw.nc.common.rabbit.model.DetailRes;

public class StringMessageProcess implements MessageProcess<String> {

	@Override
	public DetailRes process(String message) {
		System.out.println(message);
		return new DetailRes(true, "",message);
	}
}
