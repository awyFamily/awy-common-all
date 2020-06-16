package com.awy.common.web.advice;

import com.awy.common.util.model.ApiResult;
import com.awy.common.web.properties.WebConfigProperties;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.annotation.Resource;
import java.net.URI;
import java.util.List;

/**
 * @author yhw
 */
@ControllerAdvice
public class NcResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    @Resource
    private WebConfigProperties webConfigProperties;

    private static PathMatcher matcher = new AntPathMatcher();

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        if (converterType.isAssignableFrom(MappingJackson2HttpMessageConverter.class)) {
            return true;
        }
        return false;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        //如果不需要特定义转换
        if(isIgnore(request)){
            return body;
        }

        //设置不需要 转换的白名单(通过url,注解的形式)


        //自定义特定转换((通过url,注解的形式,注解更灵活一点)

        if(body instanceof ApiResult){
            return body;
        }

        if (body == null) {
            return ApiResult.ok();
        }

        return ApiResult.ok(body);
    }

    private boolean isIgnore(ServerHttpRequest request){
        if(webConfigProperties.getIgnoreAll()){
            return true;
        }

        List<String> ignoreJsonResponseUrls = webConfigProperties.getIgnoreJsonResponseUrls();
        if(ignoreJsonResponseUrls == null || ignoreJsonResponseUrls.isEmpty()){
            return false;
        }

        URI uri = request.getURI();
        //PatternMatchUtils.simpleMatch()
        for (String url : ignoreJsonResponseUrls) {
            if(matcher.match(url,uri.getPath())){
                return true;
            }
        }

        return false;
    }
}
