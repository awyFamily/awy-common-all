package com.awy.common.util;

import groovy.lang.GroovyClassLoader;
import org.junit.Test;

import java.lang.reflect.Method;

/**
 * https://www.cnblogs.com/barrywxx/p/13233373.html
 */
public class GroovyTest {

    @Test
    public void testGroovyClasses() throws Exception {
        //groovy提供了一种将字符串文本代码直接转换成Java Class对象的功能
        GroovyClassLoader groovyClassLoader = new GroovyClassLoader();
        //里面的文本是Java代码,但是我们可以看到这是一个字符串我们可以直接生成对应的Class<?>对象,而不需要我们写一个.java文件
        Class<?> clazz = groovyClassLoader.parseClass("package com.xxl.job.core.glue;\n" +
                "\n" +
                "public class Main {\n" +
                "\n" +
                "    public int age = 22;\n" +
                "    \n" +
                "    public void sayHello() {\n" +
                "        System.out.println(\"年龄是:\" + age);\n" +
                "    }\n" +
                "}\n");
        Object obj = clazz.newInstance();
        Method method = clazz.getDeclaredMethod("sayHello");
        method.invoke(obj);

        Object val = method.getDefaultValue();
        System.out.println(val);
    }
}
