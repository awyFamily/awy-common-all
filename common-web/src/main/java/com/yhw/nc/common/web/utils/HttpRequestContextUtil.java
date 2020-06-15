package com.yhw.nc.common.web.utils;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @author yhw
 */
public final class HttpRequestContextUtil {

    private HttpRequestContextUtil(){}

    public static ServletRequestAttributes getServletRequestAttributes(){
        return  (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    }

    public static HttpServletRequest getHttpServletRequest(){
        ServletRequestAttributes servletRequestAttributes = getServletRequestAttributes();
        if(servletRequestAttributes != null){
            return servletRequestAttributes.getRequest();
        }
        return null;
    }

    public static String getHeader(String key){
        HttpServletRequest request = getHttpServletRequest();
        if(request != null){
            return request.getHeader(key);
        }
        return null;
    }

    public static String getToken(){
        return getHeader("Authorization");
    }

}
