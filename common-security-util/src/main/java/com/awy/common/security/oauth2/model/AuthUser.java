package com.awy.common.security.oauth2.model;

import cn.hutool.core.collection.CollectionUtil;
import com.awy.common.util.constants.SecurityConstant;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.*;

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

    private List<String> roles;

    private List<String> permissions;

    public AuthUser(String userId, String username, String password,List<Integer> platformTypes,List<Integer> companyIds,List<String> roles,List<String> permissions){
        super(username, password, true, true, true, true, new ArrayList<>());
        this.userId = userId;
        this.platformTypes = platformTypes;
        this.companyIds = companyIds;
        this.roles = roles;
        this.permissions = permissions;
    }

    public AuthUser(String userId, String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
        this(userId,null,null,username,password,enabled,accountNonExpired,credentialsNonExpired,accountNonLocked,authorities);
    }

    public AuthUser(String userId, List<String> roles, List<String> permissions, String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
        this(userId,null,roles,permissions,username,password,enabled,accountNonExpired,credentialsNonExpired,accountNonLocked,authorities);
    }

    public AuthUser(String userId, List<Integer> companyIds, List<String> roles, List<String> permissions, String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
        this(userId,null,companyIds,roles,permissions,username,password,enabled,accountNonExpired,credentialsNonExpired,accountNonLocked,authorities);
    }

    public AuthUser(String userId, List<Integer> platformTypes, List<Integer> companyIds, List<String> roles, List<String> permissions, String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.userId = userId;
        this.platformTypes = platformTypes;
        this.companyIds = companyIds;
        this.roles = roles;
        this.permissions = permissions;
    }

    public static AuthUser getAuthoritiesMode(String userId, String username, String password,List<Integer> platformTypes,List<Integer> companyIds,List<String> auth_roles,List<String> permissions) {
        Set<GrantedAuthority> authorities = new HashSet<>();
        List<String> roles = new ArrayList<>();
        if(CollectionUtil.isNotEmpty(auth_roles)){
            for (String role : auth_roles) {
                authorities.add(new SimpleGrantedAuthority(SecurityConstant.ROLE_OAUTH_PERFIX.concat(role)));
                roles.add(SecurityConstant.ROLE_OAUTH_PERFIX.concat(role));
            }
        }
        if(CollectionUtil.isNotEmpty(permissions)){
            for (String permissionKey : permissions) {
                authorities.add(new SimpleGrantedAuthority(permissionKey));
                permissions.add(permissionKey);
            }
        }

        return new AuthUser(userId,platformTypes,companyIds, roles, permissions, username,password,
                true, true, true, true, authorities);
    }

}
