package com.awy.common.security.oauth2.endpoint;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.awy.common.security.oauth2.model.AuthUser;
import com.awy.common.security.oauth2.model.CustomAuthDTO;
import com.awy.common.security.oauth2.model.OAuth2AuthenticationVO;
import com.awy.common.util.constants.SecurityConstant;
import com.awy.common.util.utils.CollUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidClientException;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.common.exceptions.InvalidRequestException;
import org.springframework.security.oauth2.common.exceptions.UnsupportedGrantTypeException;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yhw
 * @date 2022-04-19
 */
@Slf4j
public abstract class CustomLoginAbstractEndpoint<T extends CustomAuthDTO>  {

    @Autowired
    private AuthorizationServerTokenServices authorizationServerTokenServices;
    @Autowired
    private ClientDetailsService clientDetailsService;

    /**
     * @return clientId
     */
    public abstract String getClientId();


    public OAuth2AuthenticationVO postAccessToken(T dto) {
        if (StrUtil.isEmpty(this.getClientId())) {
            log.error("client Id isEmpty. please setting clientId parameter !");
            throw new InvalidClientException("client Id isEmpty. please setting clientId parameter !");
        }

        ClientDetails authenticatedClient = clientDetailsService.loadClientByClientId(this.getClientId());

        //requestParameters
        Map<String, String> requestParameters = new HashMap<>();
        requestParameters.put("client_secret", "secret");
        TokenRequest tokenRequest = new TokenRequest(requestParameters, this.getClientId(), CollUtil.newArrayList("app"), "password");


        if (this.getClientId() != null && !this.getClientId().equals("")) {
            if (!this.getClientId().equals(tokenRequest.getClientId())) {
                throw new InvalidClientException("Given client ID does not match authenticated client");
            }
        }

        if (!StringUtils.hasText(tokenRequest.getGrantType())) {
            throw new InvalidRequestException("Missing grant type");
        }
        if (tokenRequest.getGrantType().equals("implicit")) {
            throw new InvalidGrantException("Implicit grant type not supported from token endpoint");
        }

        OAuth2Authentication oAuth2Authentication = getOAuth2Authentication(dto, authenticatedClient, tokenRequest);
        OAuth2AccessToken accessToken = authorizationServerTokenServices.createAccessToken(oAuth2Authentication);
        if (accessToken == null) {
            throw new UnsupportedGrantTypeException("Unsupported grant type: " + tokenRequest.getGrantType());
        }

        AuthUser authUser = (AuthUser) oAuth2Authentication.getPrincipal();
        return new OAuth2AuthenticationVO(accessToken,authUser.getCompanyIds(),authUser.getRoles(),authUser.getPermissions());
    }

    /**
     * refresh token
     * @param refresh_token  refresh_token
     * @return fresh token
     */
    public OAuth2AccessToken refreshAccessToken(String refresh_token) {
        Assert.isFalse(StrUtil.isBlank(refresh_token), "refresh_token is blank");

        Map<String, String> requestParameters = new HashMap<>();
        requestParameters.put(SecurityConstant.CLIENT_SECRET, "secret");
        TokenRequest tokenRequest = new TokenRequest(requestParameters, this.getClientId(), CollUtil.newArrayList("app"), OAuth2AccessToken.REFRESH_TOKEN);

        OAuth2AccessToken oAuth2AccessToken = authorizationServerTokenServices.refreshAccessToken(refresh_token, tokenRequest);
        return oAuth2AccessToken;
    }

    /**
     * get custom auth user info
     * @param dto query model
     * @return auth user info
     */
    public abstract AuthUser getAuthUser(T dto);

    /**
     * get oauth Authentication info
     * @param dto          wx dto
     * @param client       client infp
     * @param tokenRequest token request info
     * @return oauth Authentication info
     */
    private OAuth2Authentication getOAuth2Authentication(T dto, ClientDetails client, TokenRequest tokenRequest) {
        //userAuthentication
        AuthUser authUser = this.getAuthUser(dto);
        if (authUser == null) {
            throw new InvalidGrantException("user not exists");
        }

        UserDetails userDetails = AuthUser.getAuthoritiesMode(authUser);
        Authentication userAuthentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        //client info
        OAuth2Request storedOAuth2Request = new OAuth2Request(tokenRequest.getRequestParameters(), client.getClientId(), client.getAuthorities(), true, client.getScope(),
                client.getResourceIds(), null, null, null);
        return new OAuth2Authentication(storedOAuth2Request, userAuthentication);
    }

}

