package com.awy.common.log.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "com.awy.common.log")
@EnableFeignClients(basePackages = {"com.awy.common.log.service"})
public class CommonLogConfig {
}
