package com.yhw.nc.common.model;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

/**
 * 拓展Security用户认证
 * 可以在此处拓展token信息（根据自已业务需求）
 * @author
 */
@Data
public class AuthUser extends User {

    private String userId;

    private Integer companyId;

    public AuthUser(String userId,Integer companyId, String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.userId = userId;
        this.companyId = companyId;
    }
}
