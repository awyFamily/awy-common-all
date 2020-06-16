package com.awy.common.gateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * @author yhw
 */
@Configuration
public class Config {

    private static final String  AUTH_FILTER_PROPERTIES_PREFIX = "security.oauth.filter";

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
