package com.awy.common.util.service;

import cn.hutool.core.util.StrUtil;
import com.awy.common.redis.RedisComponent;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.*;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.UUID;

/**
 * 基于继承的 DefaultTokenServices
 */
public class YyDefaultTokenServices extends DefaultTokenServices {

    private TokenEnhancer yyyTokenEnhancer;

    private TokenStore yyTokenStore;

    private RedisComponent redisComponent;

    private YyDefaultTokenServices(){}

    public YyDefaultTokenServices(TokenEnhancer tokenEnhancer, TokenStore tokenStore,RedisComponent redisComponent){
        super.setTokenEnhancer(tokenEnhancer);
        super.setTokenStore(tokenStore);
        this.yyyTokenEnhancer = tokenEnhancer;
        this.yyTokenStore = tokenStore;
        this.redisComponent = redisComponent;
    }

    @Transactional
    @Override
    public OAuth2AccessToken createAccessToken(OAuth2Authentication authentication) throws AuthenticationException {
//        return super.createAccessToken(authentication);
        OAuth2AccessToken existingAccessToken = yyTokenStore.getAccessToken(authentication);
        OAuth2RefreshToken refreshToken = null;
        if (existingAccessToken != null) {
            if (existingAccessToken.isExpired()) {
                if (existingAccessToken.getRefreshToken() != null) {
                    refreshToken = existingAccessToken.getRefreshToken();
                    // The token store could remove the refresh token when the
                    // access token is removed, but we want to
                    // be sure...
                    yyTokenStore.removeRefreshToken(refreshToken);
                }
                yyTokenStore.removeAccessToken(existingAccessToken);
            }
            else {
                // Re-store the access token in case the authentication has changed
                yyTokenStore.storeAccessToken(existingAccessToken, authentication);
                System.out.println( existingAccessToken.getAdditionalInformation().get("user_id"));

                //
                getTokenAfter(authentication.getOAuth2Request().getClientId(),(String)existingAccessToken.getAdditionalInformation().get("user_id"),existingAccessToken);
                return existingAccessToken;
            }
        }

        // Only create a new refresh token if there wasn't an existing one
        // associated with an expired access token.
        // Clients might be holding existing refresh tokens, so we re-use it in
        // the case that the old access token
        // expired.
        if (refreshToken == null) {
            refreshToken = yyCreateRefreshToken(authentication);
        }
        // But the refresh token itself might need to be re-issued if it has
        // expired.
        else if (refreshToken instanceof ExpiringOAuth2RefreshToken) {
            ExpiringOAuth2RefreshToken expiring = (ExpiringOAuth2RefreshToken) refreshToken;
            if (System.currentTimeMillis() > expiring.getExpiration().getTime()) {
                refreshToken = yyCreateRefreshToken(authentication);
            }
        }

        OAuth2AccessToken accessToken = yyCreateAccessToken(authentication, refreshToken);
        yyTokenStore.storeAccessToken(accessToken, authentication);
        // In case it was modified
        refreshToken = accessToken.getRefreshToken();
        if (refreshToken != null) {
            yyTokenStore.storeRefreshToken(refreshToken, authentication);
        }

        //
//        getTokenAfter(authentication.getOAuth2Request().getClientId(),(String)existingAccessToken.getAdditionalInformation().get("user_id"),accessToken);
        return accessToken;
    }

    private OAuth2RefreshToken yyCreateRefreshToken(OAuth2Authentication authentication) {
        if (!isSupportRefreshToken(authentication.getOAuth2Request())) {
            return null;
        }
        int validitySeconds = getRefreshTokenValiditySeconds(authentication.getOAuth2Request());
        String value = UUID.randomUUID().toString();
        if (validitySeconds > 0) {
            return new DefaultExpiringOAuth2RefreshToken(value, new Date(System.currentTimeMillis()
                    + (validitySeconds * 1000L)));
        }
        return new DefaultOAuth2RefreshToken(value);
    }

    private OAuth2AccessToken yyCreateAccessToken(OAuth2Authentication authentication, OAuth2RefreshToken refreshToken) {
        DefaultOAuth2AccessToken token = new DefaultOAuth2AccessToken(UUID.randomUUID().toString());
        int validitySeconds = getAccessTokenValiditySeconds(authentication.getOAuth2Request());
        if (validitySeconds > 0) {
            token.setExpiration(new Date(System.currentTimeMillis() + (validitySeconds * 1000L)));
        }
        token.setRefreshToken(refreshToken);
        token.setScope(authentication.getOAuth2Request().getScope());

        return yyyTokenEnhancer != null ? yyyTokenEnhancer.enhance(token, authentication) : token;
    }

    private void getTokenAfter(String clientId, String userMark,OAuth2AccessToken accessToken){
        if(StrUtil.isNotBlank(clientId) && StrUtil.isNotBlank(userMark)){
            String key = clientId.concat(":").concat(userMark);

            setExpires(key,accessToken.getRefreshToken().getValue(),accessToken.getExpiresIn());
        }
    }

    private void setExpires(String key,String refreshToken,long expires){
        //如果传入的是 accessToken.getExpiration().getTime() 则需要
//        expires = expires - (new Date().getTime() / 1000);
        if(expires > 120){
            expires = expires - 60;
            redisComponent.setStrEx(key,refreshToken,expires);
        }
    }



}
