package com.yhw.nc.common.log.service;

import com.yhw.nc.common.log.listener.LogMessage;
import com.yhw.nc.common.model.ApiResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author
 */
@FeignClient(value = "server-user-biz", contextId="logController", path = "/logRemote")
public interface ILogService {

    /**
     * 添加日志
     * @param logMessage 实体
     * @return res
     */
    @PostMapping("/add")
    ApiResult saveLog(@RequestBody LogMessage logMessage);
}
