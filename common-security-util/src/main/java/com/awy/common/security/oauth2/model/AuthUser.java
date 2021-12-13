package com.awy.common.security.oauth2.model;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.List;

/**
 * 拓展Security用户认证
 * 可以在此处拓展token信息（根据自已业务需求）
 * @author
 */
@Data
public class AuthUser extends User {

    private String userId;

    private List<Integer> platformTypes;

    private List<Integer> companyIds;

    public AuthUser(String userId,List<Integer> platformTypes,List<Integer> companyIds, String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.userId = userId;
        this.platformTypes = platformTypes;
        this.companyIds = companyIds;
    }

}
