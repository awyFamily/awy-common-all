package com.awy.common.log.listener;

import com.awy.common.log.service.ILogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 日志监听事件
 * @author yhw
 */
@Slf4j
@Component
public class LogListenerEvent implements ApplicationListener<LogNoteEvent> {

    @Resource
    private ILogService logService;


    @Async
    @Override
    public void onApplicationEvent(LogNoteEvent event) {
        log.info(Thread.currentThread().getName()+"---:进入日志事件监听器................................");
        logService.saveLog(event.getLogMessage());
    }
}
