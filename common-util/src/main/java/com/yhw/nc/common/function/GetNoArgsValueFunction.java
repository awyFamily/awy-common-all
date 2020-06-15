package com.yhw.nc.common.function;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;


/**
 * 通过此类可以使用 lambda 方式获取无参方法内容
 */
@FunctionalInterface
public interface GetNoArgsValueFunction {

    /**
     * 这里只接收无参方法
     * @return 传入的值
     */
    Object get();

    default SerializedLambda getSerializedLambda() throws Exception {
        //writeReplace改了好像会报异常
        Method write = this.getClass().getDeclaredMethod("writeReplace");
        write.setAccessible(true);
        //获取 SerializedLambda 是重点
        return (SerializedLambda) write.invoke(this);
    }

    default String getImplClass() {
        try {
            //返回实现类名
            return getSerializedLambda().getImplClass();
        } catch (Exception e) {
            return null;
        }
    }

    default String getImplMethodName() {
        try {
            //返回方法名称
            return getSerializedLambda().getImplMethodName();
        } catch (Exception e) {
            return null;
        }
    }
}
