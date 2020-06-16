package com.awy.common.log.aspect;


import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.awy.common.log.annotation.CloudLog;
import com.awy.common.log.listener.LogNoteEvent;
import com.awy.common.log.listener.LogMessage;
import com.awy.common.util.model.AuthUser;
import com.awy.common.util.utils.DateJdK8Util;
import com.awy.common.util.utils.SecurityUtil;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.ClassUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 定义日志通知
 * 实现方法拦截器
 * @author yhw
 */
public class LogInterceptor implements MethodInterceptor {

    public LogInterceptor(ApplicationEventPublisher eventPublisher){
        this.eventPublisher = eventPublisher;
    }

    private ApplicationEventPublisher eventPublisher;

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        //获取目标类
        Class<?> targetClass = (invocation.getThis() != null ? AopUtils.getTargetClass(invocation.getThis()) : null);
        //获取指定方法
        Method specificMethod = ClassUtils.getMostSpecificMethod(invocation.getMethod(), targetClass);
        //获取真正执行的方法,可能存在桥接方法
        final Method userDeclaredMethod = BridgeMethodResolver.findBridgedMethod(specificMethod);


        //类名称(没有包名)
//        System.out.println("class SimpleName" + userDeclaredMethod.getSimpleName());
        //前置增强逻辑
        System.out.println("before我进来了");
        //获取方法上注解
        CloudLog cloudLog = AnnotatedElementUtils.findMergedAnnotation(userDeclaredMethod, CloudLog.class);
        if (cloudLog == null) {
            //类上是否标注 -> 注解
            cloudLog = AnnotatedElementUtils.findMergedAnnotation(userDeclaredMethod.getDeclaringClass(), CloudLog.class);
        }

        //类名 + 方法名
        String methodName = userDeclaredMethod.getDeclaringClass().getSimpleName().concat(".").concat(userDeclaredMethod.getName());

        //用户信息
        AuthUser authUser;
        String username = "";
        String userId = "";
        if((authUser = SecurityUtil.getUser()) != null){
            username = authUser.getUsername();
            userId = authUser.getUserId();
        }

        //通过swagger 获取方法标注的信息(可拓展)
        //ApiOperation apiOperation = AnnotatedElementUtils.findMergedAnnotation(userDeclaredMethod, ApiOperation.class);

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        eventPublisher.publishEvent(new LogNoteEvent(true,
//                new LogMessage(userDeclaredMethod.getName(),cloudLog.logType().getCode(),SecurityUtil.getUser().getUsername(),SecurityUtil.getCurrentUserId(),getIpAddr(request),cloudLog.remark())));
                new LogMessage(methodName,cloudLog.logType().getCode(),username,userId,getIpAddr(request),cloudLog.remark())));

        StringBuilder parameter = new StringBuilder();
        Object[] arguments = invocation.getArguments();
        if(arguments != null){
            int parameterSize = arguments.length;
            if(parameterSize == 1){
                parameter.append(formatObj(arguments[0]));
            }else {
                for(int i = 0; i < parameterSize; i++){
                    parameter.append(formatObj(arguments[i]));
                    parameter.append(",");
                }
            }
        }

        //获取返回类型
        /*Class<?> returnType = invocation.getMethod().getReturnType();
        //返回类型判断
        if (User.class.isAssignableFrom(returnType)) {

            return null;
        }*/

        //执行具体业务逻辑
        Object proceed = invocation.proceed();
        //执行后置增强逻辑（异常逻辑执行不了）
        System.out.println("after我进来了");
        return proceed;
    }

    /**
     * 获取参数
     * @param argument
     * @return
     */
    private String formatObj(Object argument){
        String formatStr = "";
        try {
            if(argument instanceof Number){
                formatStr =   argument.toString();
            }else if(argument instanceof String){
                formatStr =  argument.toString();
            }else if(argument instanceof Iterable){
                formatStr =  argument.toString();
            }else if(argument instanceof Date){
                formatStr =  DateUtil.formatDate((Date) argument);
            }else if(argument instanceof File){
                //TODO
            }else if(argument instanceof LocalDate){
                formatStr = DateJdK8Util.formatLocalDate((LocalDate) argument);
            }else if(argument instanceof LocalDateTime){
                formatStr = DateJdK8Util.formatLocalDateTime((LocalDateTime) argument);
            }else if(argument instanceof Boolean){
                formatStr = argument.toString();
            }else {
                formatStr = JSONUtil.parseObj(argument).toString();
            }
            return formatStr;
        } catch (Exception e) {
            return argument.toString();
        }
    }

    /**
     * 获取客户端IP地址
     * reference: https://blog.csdn.net/kioo_i_see/article/details/71630014
     */
    private String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
            if("127.0.0.1".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip)){
                ip = getHostAddress(ip);
            }
        }
        // 多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        if(ip != null && ip.length() > 15){
            if(ip.indexOf(",")>0){
                ip = ip.substring(0,ip.indexOf(","));
            }
        }
        return ip;
    }

    /**
     * 根据网卡取本机配置的IP
     * @param ip
     * @return
     */
    private String getHostAddress(String ip){
        InetAddress inet=null;
        try {
            inet = InetAddress.getLocalHost();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return inet.getHostAddress();
    }

}
