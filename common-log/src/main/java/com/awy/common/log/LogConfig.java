package com.awy.common.log;

import com.awy.common.log.annotation.CloudLog;
import com.awy.common.log.aspect.LogAdvisor;
import com.awy.common.log.aspect.LogInterceptor;
import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

/**
 * 日志aop配置(需要依赖security)
 * @author yhw
 */
@EnableAsync
@Component
@Configurable
public class LogConfig {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    //需要实现多个切入点，则增加多个bean
    @Bean
    public LogAdvisor logAdvisor(){
        MethodInterceptor methodInterceptor = new LogInterceptor(eventPublisher);
        LogAdvisor logAdvisor = new LogAdvisor(CloudLog.class,methodInterceptor);
        logAdvisor.setOrder(-100);
        return logAdvisor;
    }
}
