package com.awy.common.web.aspect;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.LRUCache;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.awy.common.util.model.ApiResult;
import com.awy.common.redis.RedisComponent;
import com.awy.common.util.utils.JsonUtil;
import com.awy.common.util.utils.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 先使用spring 注解实现，后续使用 实现 MethodInterceptor 接口实现
 * @author yhw
 */
@Slf4j
@Aspect
@Component
public class ControllerAspect implements Ordered {

    private static final String JOIN_KEY = ":";

    @Autowired
    private RedisComponent redisComponent;

    private static LRUCache<String, Byte> ignoreCache = CacheUtil.newLRUCache(100);

    @Pointcut("execution(* com.awy..*.controller..*.*(..)) || execution(* com.awy..*.facade..*.*(..))")
    public void pointCut() {}

    @Around("pointCut()")
    public Object controllerAround(ProceedingJoinPoint proceedingJoinPoint) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        //目的是放行内部调用,避免序列化异常
        String authorization = request.getHeader("Authorization");
        if(authorization != null && !authorization.isEmpty()){
            String parameterKey = getParameterKey(proceedingJoinPoint);
            //放行序列话出错的异常
            if(ignoreCache.get(parameterKey) != null){
                return getProceed(proceedingJoinPoint);
            }
            if(!"".equals(parameterKey)){
                String proceedStr =null;
                try{
                    proceedStr = redisComponent.getStr(parameterKey);
                }catch (Exception e){
                    log.error("redis connection error",e);
                }
                if(proceedStr != null && !proceedStr.isEmpty()){
                    try {
                        return JsonUtil.fromJson(proceedStr, ApiResult.class);
                    } catch (Exception e) {
                        ignoreCache.put(parameterKey,(byte)0);
                        return getProceed(proceedingJoinPoint);
                    }
                }
            }

            Object proceed = getProceed(proceedingJoinPoint);
            if(proceed != null){
                try{
                    redisComponent.setStrEx(parameterKey, JsonUtil.toJson(proceed), 3);
                }catch (Exception e){
                    log.error("redis connection error",e);
                    ignoreCache.put(parameterKey,(byte)0);
                }
            }
            return proceed;
        }
        return getProceed(proceedingJoinPoint);
    }

    /**
     * 实际方法调用
     * @param proceedingJoinPoint
     * @return 调用结果
     */
    private Object getProceed(ProceedingJoinPoint proceedingJoinPoint){
        Object proceed = null;
        try {
            proceed = proceedingJoinPoint.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return proceed;
    }

    /**
     * 获取参数key
     * @param proceedingJoinPoint
     * @return
     */
    private String getParameterKey(ProceedingJoinPoint proceedingJoinPoint){
        String currentUserId = SecurityUtil.getCurrentUserId();
        StringBuilder parameterKey = new StringBuilder();
        if(StrUtil.isNotBlank(currentUserId)){
            parameterKey.append(currentUserId).append(JOIN_KEY);
        }
        try{
            String className = proceedingJoinPoint.getSignature().getDeclaringTypeName();
            String[] split = StrUtil.split(className, ".");
            String lastClassName = split[split.length -1];
            String methodName = proceedingJoinPoint.getSignature().getName();

            parameterKey.append(lastClassName);
            parameterKey.append(JOIN_KEY);
            parameterKey.append(methodName);
            parameterKey.append(JOIN_KEY);


            MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
            //参数名称
            String[] parameterNames = methodSignature.getParameterNames();
            //参数值
            Object[] args = proceedingJoinPoint.getArgs();

            Object objects;
            JSONObject jsonObject;
            for (int i = 0; i < args.length; i++) {
                objects = args[i];
                if(objects != null){
                    parameterKey.append(parameterNames[i]).append(":");
                    if(objects instanceof String){
                        parameterKey.append(objects);
                    }else if(objects instanceof Number){
                        parameterKey.append(objects);
                    }else if(objects instanceof Date){
                        parameterKey.append(objects);
                    }else if(objects instanceof LocalDate){
                        parameterKey.append(objects);
                    }else if(objects instanceof LocalDateTime){
                        parameterKey.append(objects);
                    }else if(objects instanceof List){
                        parameterKey.append(objects);
                    }else if(objects instanceof Arrays){
                        parameterKey.append(objects);
                    }else{
                        try {
                            jsonObject = JSONUtil.parseObj(objects);
                            for(String key : jsonObject.keySet()){
                                parameterKey.append(jsonObject.get(key)).append(":");
                            }
                        } catch (Exception e) {
                            parameterKey.append(objects);
                        }
                    }
                }
            }
        }catch (Exception e){
            return "";
        }
        return parameterKey.toString();
    }

    @Override
    public int getOrder() {
        return -100;
    }
}
