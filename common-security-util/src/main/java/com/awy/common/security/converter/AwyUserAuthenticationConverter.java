package com.awy.common.security.converter;

import com.awy.common.util.constants.SecurityConstant;
import com.awy.common.security.oauth2.model.AuthUser;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.provider.token.UserAuthenticationConverter;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 自定义token转换器
 * @author yhw
 */
public class AwyUserAuthenticationConverter implements UserAuthenticationConverter {



    /**
     * Extract information about the user to be used in an access token (i.e. for resource servers).
     *
     * @param authentication an authentication representing a user
     * @return a map of key values representing the unique information about the user
     */
    @Override
    public Map<String, ?> convertUserAuthentication(Authentication authentication) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put(USERNAME, authentication.getName());

        Object principal = authentication.getPrincipal();
        AuthUser authUser;
        if(principal instanceof  AuthUser){
            authUser =   (AuthUser)principal;
            response.put(SecurityConstant.USER_ID, authUser.getUserId());
            response.put(SecurityConstant.COMPANY_IDS, authUser.getCompanyIds());
            response.put(SecurityConstant.PLAT_FROM_TYPE, authUser.getPlatformTypes());
        }

        if (authentication.getAuthorities() != null && !authentication.getAuthorities().isEmpty()) {
            response.put(AUTHORITIES, AuthorityUtils.authorityListToSet(authentication.getAuthorities()));
        }
        return response;
    }


    /**
     * Inverse of {@link #convertUserAuthentication(Authentication)}. Extracts an Authentication from a map.
     *
     * @param map a map of user information
     * @return an Authentication representing the user or null if there is none
     */
    @Override
    public Authentication extractAuthentication(Map<String, ?> map) {
        if (map.containsKey(USERNAME)) {
            Collection<? extends GrantedAuthority> authorities = getAuthorities(map);


            String username = (String) map.get(USERNAME);
            String userId = (String)map.get(SecurityConstant.USER_ID);
            List<Integer> platformTypes = (List<Integer>)map.get(SecurityConstant.PLAT_FROM_TYPE);
            List<Integer> companyIds = (List<Integer>)map.get(SecurityConstant.COMPANY_IDS);

            //Security User增强类
            AuthUser user = new AuthUser(userId, platformTypes,companyIds, username, SecurityConstant.N_A,
                    true, true, true, true, authorities);
            return new UsernamePasswordAuthenticationToken(user, SecurityConstant.N_A, authorities);
        }
        return null;
    }


    private Collection<? extends GrantedAuthority> getAuthorities(Map<String, ?> map) {
        Object authorities = map.get(AUTHORITIES);
        if (authorities instanceof String) {
            return AuthorityUtils.commaSeparatedStringToAuthorityList((String) authorities);
        }
        if (authorities instanceof Collection) {
            return AuthorityUtils.commaSeparatedStringToAuthorityList(StringUtils
                    .collectionToCommaDelimitedString((Collection<?>) authorities));
        }
        throw new IllegalArgumentException("Authorities must be either a String or a Collection");
    }
}
