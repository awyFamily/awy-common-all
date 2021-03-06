package com.awy.common.gateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * @author yhw
 */
@Configuration
@ComponentScan(basePackages = "com.awy.common.gateway")
public class CommonGatewayConfig {

    private static final String  AUTH_FILTER_PROPERTIES_PREFIX = "awy.cloud.gateway.oauth.filter";

    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }

    @ConfigurationProperties(prefix=AUTH_FILTER_PROPERTIES_PREFIX)
    @Bean
    public AuthFilterProperties authFilterProperties(){
        return new AuthFilterProperties();
    }



}
