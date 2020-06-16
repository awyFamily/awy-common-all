package com.awy.common.util.service;

import com.awy.common.util.constants.SecurityConstant;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

/**
 * 获取 token后 拓展信息
 * @author yhw
 */
public interface IExpandTokenService {

    void getTokenAfter(OAuth2Authentication authentication, OAuth2AccessToken accessToken);

    void refreshTokenAfter(OAuth2Authentication authentication, OAuth2AccessToken accessToken);


    default String getClientId(OAuth2Authentication authentication) {
        if(authentication == null){
            return null;
        }
        return authentication.getOAuth2Request().getClientId();
    }

    default String getUserMark(OAuth2AccessToken accessToken) {
        if(accessToken == null){
            return null;
        }
        return (String)accessToken.getAdditionalInformation().get(SecurityConstant.USER_ID);
    }

    //一个map 存clientId
    //${存clientId}:${userId}

}
