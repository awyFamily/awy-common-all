package com.awy.common.util.utils;

import com.awy.common.security.oauth2.model.AuthUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;

public class SecurityUtil {

    private SecurityUtil(){}

    public static String getCurrentUserId(){
        AuthUser authUser = getUser();
        if(authUser != null){
            return authUser.getUserId();
        }
        return "";
    }

    public static List<Integer> getCurrentCompanyIds(){
        AuthUser authUser = getUser();
        if(authUser != null){
            return authUser.getCompanyIds();
        }
        return new ArrayList<>();
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
