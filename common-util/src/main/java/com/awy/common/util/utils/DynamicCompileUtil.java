package com.awy.common.util.utils;

import cn.hutool.core.util.ClassUtil;
import groovy.lang.GroovyClassLoader;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author yhw
 * @date 2021/08/24
 */
public final class DynamicCompileUtil {

    private final static GroovyClassLoader groovyClassLoader = new GroovyClassLoader();

    public static Object dynamicExe(){
        //commonComputeClassStr 可以配置到数据库中
        String thisStr = commonComputeClassStr.replace("P1","30.175");
        thisStr = thisStr.replace("P2","0.664");
        thisStr = thisStr.replace("P3","0.183");
        thisStr = thisStr.replace("E1","35");
        thisStr = thisStr.replace("E2","22");
        Class<?> clazz = groovyClassLoader.parseClass(thisStr);
        try {
            Object obj = clazz.newInstance();
            Method sayHello = ClassUtil.getDeclaredMethod(clazz, "getValue");
            return sayHello.invoke(obj);
        } catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static final String commonComputeClassStr = "package com.awy.common.util;\n" +
            "\n" +
            "import java.math.BigDecimal;\n" +
            "import java.math.RoundingMode;\n" +
            "\n" +
            "public class CommonCompute {\n" +
            "\n" +
            "    public BigDecimal getValue(){\n" +
            "        Double value = Math.pow(2.71828183,P1 - P2 * E1 - P3 * E2);\n" +
            "        BigDecimal bigDecimal = new BigDecimal(1);\n" +
            "        bigDecimal = bigDecimal.divide(BigDecimal.valueOf(value),10, RoundingMode.HALF_DOWN);\n" +
            "        return bigDecimal;\n" +
            "    }\n" +
            "    \n" +
            "}\n";

}
