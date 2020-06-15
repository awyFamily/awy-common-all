package com.yhw.nc.common.security.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/actuator/**", "/token/**")
                .permitAll()
                .anyRequest()
                .permitAll()
                .and()
                .csrf()
                .disable();
    }

    //资源服务器客户端不需要此配置
    /*public AuthenticationManager authenticationManagerBean() {
        return super.authenticationManagerBean();
    }*/

}
