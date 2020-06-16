package com.awy.common.util.service;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.awy.common.redis.RedisComponent;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import java.util.Date;

/**
 * 简单的获取token 后 拓展方法
 */
public class ExampleExpandTokenService implements IExpandTokenService {


    private ExampleExpandTokenService(){

    }

    private RedisComponent redisComponent;

    public ExampleExpandTokenService(RedisComponent redisComponent){
        Assert.isFalse(redisComponent == null, "redis component is empty");
        this.redisComponent = redisComponent;
    }

//    @Value("${spring.security.oauth2.client.client_secret}")
//    private String secret;

    @Override
    public void getTokenAfter(OAuth2Authentication authentication, OAuth2AccessToken accessToken) {
        String clientId  = this.getClientId(authentication);
        String userMark = this.getUserMark(accessToken);

        if(StrUtil.isNotBlank(clientId) && StrUtil.isNotBlank(userMark)){
            String key = clientId.concat(":").concat(userMark);
            setExpires(key,accessToken.getRefreshToken().getValue(),accessToken.getExpiration().getTime());
        }
    }

    @Override
    public void refreshTokenAfter(OAuth2Authentication authentication, OAuth2AccessToken accessToken) {
    }


    private void setExpires(String key,String refreshToken,long expires){
        expires = expires - (new Date().getTime() / 1000);
        if(expires > 120){
            expires = expires - 60;
            redisComponent.setStrEx(key,refreshToken,expires);
        }
    }

}
