package com.awy.common.security.oauth2.endpoint;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidClientException;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.common.exceptions.InvalidRequestException;
import org.springframework.security.oauth2.common.exceptions.UnsupportedGrantTypeException;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerEndpointsConfiguration;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2RequestValidator;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.endpoint.AbstractEndpoint;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestValidator;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * base login endpoint
 * @author yhw
 */
public abstract class LoginAbstractEndpoint extends AbstractEndpoint {

    @Autowired
    private AuthorizationServerEndpointsConfiguration endpoints;
    @Autowired
    private ClientDetailsService clientDetailsService;

    /**
     * @return clientId
     */
    public abstract String getClientId();

    private OAuth2RequestValidator oAuth2RequestValidator = new DefaultOAuth2RequestValidator();

    @Override
    public void afterPropertiesSet() throws Exception {
        super.setTokenGranter(endpoints.getEndpointsConfigurer().getTokenGranter());
        super.setClientDetailsService(clientDetailsService);
        super.afterPropertiesSet();
    }


    public OAuth2AccessToken getOAuth2AccessToken(String username,String password)  {

        Map<String, String> parameters = getParameters(username,password);

        ClientDetails authenticatedClient = getClientDetailsService().loadClientByClientId(this.getClientId());

        TokenRequest tokenRequest = getOAuth2RequestFactory().createTokenRequest(parameters, authenticatedClient);

        if (this.getClientId() != null && !this.getClientId().equals("")) {
            if (!this.getClientId().equals(tokenRequest.getClientId())) {
                throw new InvalidClientException("Given client ID does not match authenticated client");
            }
        }

        if (authenticatedClient != null) {
            oAuth2RequestValidator.validateScope(tokenRequest, authenticatedClient);
        }

        if (!StringUtils.hasText(tokenRequest.getGrantType())) {
            throw new InvalidRequestException("Missing grant type");
        }

        if (tokenRequest.getGrantType().equals("implicit")) {
            throw new InvalidGrantException("Implicit grant type not supported from token endpoint");
        }

        if (isAuthCodeRequest(parameters)) {
            if (!tokenRequest.getScope().isEmpty()) {
                logger.debug("Clearing scope of incoming token request");
                tokenRequest.setScope(Collections.<String> emptySet());
            }
        }

        if (isRefreshTokenRequest(parameters)) {
            tokenRequest.setScope(OAuth2Utils.parseParameterList(parameters.get(OAuth2Utils.SCOPE)));
        }

        OAuth2AccessToken token = getTokenGranter().grant(tokenRequest.getGrantType(), tokenRequest);
        if (token == null) {
            throw new UnsupportedGrantTypeException("Unsupported grant type: " + tokenRequest.getGrantType());
        }
        return token;
    }

    private Map<String, String>  getParameters(String username,String password){
        Map<String, String> parameters = new HashMap<>();
        parameters.put("username",username);
        parameters.put("password",password);
        parameters.put("client_id",this.getClientId());
        parameters.put("scope","app");
        parameters.put("grant_type","password");
        return parameters;
    }


    private boolean isRefreshTokenRequest(Map<String, String> parameters) {
        return OAuth2AccessToken.REFRESH_TOKEN.equals(parameters.get("grant_type")) && parameters.get(OAuth2AccessToken.REFRESH_TOKEN) != null;
    }

    private boolean isAuthCodeRequest(Map<String, String> parameters) {
        return "authorization_code".equals(parameters.get("grant_type")) && parameters.get("code") != null;
    }

}

