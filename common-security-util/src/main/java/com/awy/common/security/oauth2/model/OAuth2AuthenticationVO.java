package com.awy.common.security.oauth2.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author yhw
 * @date 2022-04-19
 */
@NoArgsConstructor
@Data
public class OAuth2AuthenticationVO {

    private String access_token;
    private String token_type;
    private String refresh_token;
    private Set<String> scope;
    private String license;
    private String plat_from_type;
    private String user_id;
    private List<Integer> company_id;
    private List<String> permissions;
    private List<String> roles;
    /**
     * 预留拓展
     */
    private Map<String,Object> expand;

    public OAuth2AuthenticationVO(OAuth2AccessToken accessToken) {
        this(accessToken,new ArrayList<>(),new ArrayList<>(),new ArrayList<>());
    }

    public OAuth2AuthenticationVO(OAuth2AccessToken accessToken, List<Integer> companyIds, List<String> roles, List<String> permissions) {
        this.access_token = accessToken.getValue();
        this.token_type = accessToken.getTokenType();
        this.refresh_token = accessToken.getRefreshToken().getValue();
        this.scope = accessToken.getScope();
        this.license = accessToken.getAdditionalInformation().get("license") != null ? accessToken.getAdditionalInformation().get("license").toString() : null;
        this.user_id = accessToken.getAdditionalInformation().get("user_id") != null ? accessToken.getAdditionalInformation().get("user_id").toString() : null;

        this.company_id = companyIds;
        this.roles = roles;
        this.permissions = permissions;
    }
}
