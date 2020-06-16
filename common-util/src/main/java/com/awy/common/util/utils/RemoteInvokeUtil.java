package com.awy.common.util.utils;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * remote invoke util
 * @author yhw
 */
@Slf4j
public class RemoteInvokeUtil {

    private RemoteInvokeUtil(){}

    /**
     * get remote invoke Result（default retry thrice, retry  30 s sleep)
     * @param object service object
     * @param methodName method name
     * @param parameters parameter array
     * @param <T> return entity
     * @return generic result
     */
    @Deprecated
    public static  <T> T getResult(Object object,String methodName,Object...parameters){
        return getResult(object,methodName,3,parameters);
    }

    /**
     * get remote invoke result (default timeout 30 s)
     * @param object service object
     * @param methodName methodName
     * @param retry retry number
     * @param parameters parameter array
     * @param <T> return entity
     * @return generic result
     */
    @Deprecated
    public static <T> T getResult(Object object, String  methodName, int retry, Object...parameters){
        return getResult(object,methodName,retry,30,parameters);
    }

    /**
     * get remote invoke result (default timeout unit is seconds)
     * @param object service object
     * @param methodName method name
     * @param retry retry number
     * @param timeOut timeout
     * @param parameters parameter array
     * @param <T> return entity
     * @return generic result
     */
    @Deprecated
    public static <T> T getResult(Object object, String  methodName, int retry, int timeOut, Object...parameters){
        return getResult(object,methodName,retry,timeOut, TimeUnit.SECONDS,parameters);
    }

    /**
     * get remote invoke result
     * @param object service object
     * @param methodName methodName
     * @param retry retry number
     * @param timeOut timeout
     * @param unit timeout unit
     * @param parameters parameter
     * @param <T> return entity
     * @return generic result
     */
    @Deprecated
    public static <T> T getResult(Object object,String  methodName, int retry, int timeOut, TimeUnit unit, Object...parameters){
        Method method = getMethod(object,methodName,parameters);
        if(method == null){
            return null;
        }
        return getResult(object,method,retry,timeOut,unit,parameters);
    }

    @Deprecated
    private static <T> T getResult(Object object,Method method,int retry, int timeOut, TimeUnit unit, Object...parameters){
        if(retry <= 0 ){
            return null;
        }
        try {
            return (T)method.invoke(object,parameters);
        }catch (Exception e){
            log.error("remote invoke error",e);
            e.printStackTrace();
            retrySleep(timeOut,unit);
            return getResult(object,method,--retry,timeOut*2,unit,parameters);
        }
    }


    private static Method getMethod(Object object, String methodName, Object...parameters){
        if(object == null || StrUtil.isEmpty(methodName)){
            log.error("methodName is exists");
            return null;
        }

        Method[] declaredMethods = object.getClass().getDeclaredMethods();
        List<Method> methods = Stream.of(declaredMethods).filter(obj -> methodName.equals(obj.getName())).collect(Collectors.toList());
        if(CollectionUtil.isEmpty(methods)){
            log.error("current method not exists");
            return null;
        }

        if(methods.size() > 1){
            methods = methods.stream().filter(obj -> parameters.length == obj.getParameters().length).limit(1).collect(Collectors.toList());
            if(CollectionUtil.isEmpty(methods)){
                log.error("current method not exists");
                return null;
            }
            return methods.get(0);
        }
        return methods.get(0);
    }

    /**
     * get remote invoke Result（default retry thrice, retry  30 s sleep)
     * @param supplier execute function a supplier
     * @param <T> class type
     * @return T
     */
    public static <T> T getResult(Supplier<T> supplier){
        return getResult(supplier,3);
    }

    /**
     * get remote invoke Result
     * @param supplier execute function a supplier
     * @param retry retry number
     * @param <T>  class type
     * @return T
     */
    public static <T> T getResult(Supplier<T> supplier,int retry){
        return getResult(supplier,retry,10,TimeUnit.SECONDS);
    }

    /**
     * get remote invoke Result
     * @param supplier execute function a supplier
     * @param retry retry number
     * @param timeOut long time
     * @param unit  time unit
     * @param <T>  class type
     * @return T
     */
    public static <T> T getResult(Supplier<T> supplier,int retry,int timeOut, TimeUnit unit){
        return getResult(supplier,retry,retry,timeOut,unit);
    }

    private static <T> T getResult(Supplier<T> supplier,int retry,int retryCount, int timeOut, TimeUnit unit){
        if(retry <= 0 ){
            log.error("" + retryCount + "retries  failed ");
            return null;
        }
        T result;
        try {
            result = supplier.get();
        } catch (Exception e) {
            log.error("error detail:",e);
            if((retryCount - retry) > 0 ){
                log.error("begin make " + (retryCount - retry) + " retry ");
            }
            retrySleep(timeOut,unit);
            return getResult(supplier,--retry,retryCount,timeOut*2,unit);
        }
        return result;
    }

    private static void retrySleep(int timeOut, TimeUnit unit){
        try {
            unit.sleep(timeOut);
        } catch (InterruptedException ex) {
            log.error("",ex);
        }
    }


}
