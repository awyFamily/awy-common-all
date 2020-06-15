package com.yhw.nc.common.utils;

import com.yhw.nc.common.model.AuthUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {

    private SecurityUtil(){}

    public static String getCurrentUserId(){
        AuthUser authUser = getUser();
        if(authUser != null){
            return authUser.getUserId();
        }
        return "";
    }

    public static Integer getCurrentCompanyId(){
        AuthUser authUser = getUser();
        if(authUser != null){
            return authUser.getCompanyId();
        }
        return 0;
    }


    public static AuthUser getUser(){
        Authentication authentication = getAuthentication();
        if(authentication == null){
            return null;
        }
        AuthUser authUser = null;
        Object principal = authentication.getPrincipal();
        if(principal instanceof  AuthUser){
            authUser =   (AuthUser)principal;
        }
        return authUser;
    }

    public static Authentication getAuthentication(){
        SecurityContext context = SecurityContextHolder.getContext();
        if(context == null){
            return null;
        }
        return context.getAuthentication();
    }
}
