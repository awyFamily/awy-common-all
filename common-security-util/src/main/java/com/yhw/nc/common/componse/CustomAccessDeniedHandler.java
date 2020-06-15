package com.yhw.nc.common.componse;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.yhw.nc.common.model.ApiResult;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 权限不足
 * @author yhw
 */
@Component("customAccessDeniedHandler")
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setHeader("Content-Type", "application/json;charset=UTF-8");
        try {
            JSONObject jsonObject = JSONUtil.parseObj(ApiResult.getBuilder()
                    .setMessage("服务认证失败,权限不足")
                    .setCode(401)
                    .setSuccess(false)
                    .builder());
            response.getWriter().write(jsonObject.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
