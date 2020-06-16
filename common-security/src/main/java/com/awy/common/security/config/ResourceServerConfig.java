package com.awy.common.security.config;

import com.awy.common.security.NcUserAuthenticationConverter;
import com.awy.common.security.model.RemoteTokenProperties;
import com.awy.common.util.componse.AuthExceptionEntryPoint;
import com.awy.common.util.componse.CustomAccessDeniedHandler;
import com.awy.common.util.constants.SecurityConstant;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.AccessTokenConverter;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;

import javax.annotation.Resource;

/**
 * 资源服务(每一个服务相当于一个资源服务器)
 * 此处只配置资源服务
 */
@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    @Resource
    private AuthExceptionEntryPoint authExceptionEntryPoint;
    @Resource
    private CustomAccessDeniedHandler customAccessDeniedHandler;

    @Bean
    @ConfigurationProperties(prefix = SecurityConstant.AUTH_SCAN_PACKAGE_PREFIX)
    public RemoteTokenProperties remoteTokenProperties() {
        return new RemoteTokenProperties();
    }


    @Bean
    public AccessTokenConverter accessTokenConverter() {
        DefaultAccessTokenConverter accessTokenConverter = new DefaultAccessTokenConverter();
        NcUserAuthenticationConverter userTokenConverter = new NcUserAuthenticationConverter();
        //setting user token converter
        accessTokenConverter.setUserTokenConverter(userTokenConverter);
        return accessTokenConverter;
    }


    //Can self-determination
    @Bean
    public ResourceServerTokenServices remoteTokenServices(){
        RemoteTokenServices remoteTokenServices = new RemoteTokenServices();

        RemoteTokenProperties properties = remoteTokenProperties();
        remoteTokenServices.setCheckTokenEndpointUrl(properties.getCheck_token_uri());
        remoteTokenServices.setClientId(properties.getClient_id());
        remoteTokenServices.setClientSecret(properties.getClient_secret());
        //use default converter token
        remoteTokenServices.setAccessTokenConverter(accessTokenConverter());
        return remoteTokenServices;
    }


    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.tokenServices(remoteTokenServices())
                .resourceId(SecurityConstant.RESOURCE_CLIENT)
                .authenticationEntryPoint(authExceptionEntryPoint)
                .accessDeniedHandler(customAccessDeniedHandler)
                //资源都需要token认证
                .stateless(true);
    }


    /**
     * 配置资源服务器权限拦截
     * @param http
     * @throws Exception
     */
    @Override
    public void configure(HttpSecurity http) throws Exception {
        //允许使用iframe 嵌套，避免swagger-ui 不被加载的问题
        http.headers().frameOptions().disable();
        ExpressionUrlAuthorizationConfigurer<HttpSecurity>
                .ExpressionInterceptUrlRegistry registry = http
                .authorizeRequests();

//        /swagger-resources/**  /webjars/**  /swagger-ui.html  /swagger-resources  /v2/api-docs
        registry.antMatchers(remoteTokenProperties().getIgnore_uri()).permitAll();


        registry.anyRequest().authenticated()
                .and().csrf().disable();
        super.configure(http);
    }
}
