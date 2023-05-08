package com.awy.common.util.componse;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.awy.common.util.model.ApiResult;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 自定义token认证异常
 * @author yhw
 */
@Component("authExceptionEntryPoint")
public class AuthExceptionEntryPoint implements AuthenticationEntryPoint {


    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) {
        Throwable cause = authException.getCause();

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setHeader("Content-Type", "application/json;charset=UTF-8");
        try {
            if(cause instanceof InvalidTokenException) {

                JSONObject  jsonObject = JSONUtil.parseObj(ApiResult.getBuilder()
                        .setMessage("服务认证失败,token无效")
                        .setCode(401)
                        .setSuccess(false)
                        .builder());
                response.getWriter().write(jsonObject.toString());
            }else{
                JSONObject  jsonObject = JSONUtil.parseObj(ApiResult.getBuilder()
                        .setMessage("服务认证失败,token无效")
                        .setCode(401)
                        .setSuccess(false)
                        .builder());
                response.getWriter().write(jsonObject.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
