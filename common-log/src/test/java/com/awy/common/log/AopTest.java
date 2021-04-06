package com.awy.common.log;

import com.awy.common.log.annotation.CloudLog;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.security.util.MethodInvocationUtils;
import org.springframework.security.util.SimpleMethodInvocation;

import java.util.HashMap;
import java.util.Map;

public class AopTest {

    @CloudLog
    public String test1(){
        System.out.println("aaaaaaa");
        return "a";
    }

    public String test2(){
        System.out.println("bbbb");
        return "b";
    }

    public static void main1() {
        /*AopTest test = new AopTest();
        test.test1();
        test.test2();

        LogAdvisor advisor = new LogAdvisor(CloudLog.class, new LogInterceptor(null));
        test = new AopTest();
        test.test1();
        test.test2();*/

        SpelParserConfiguration spelParserConfiguration = new SpelParserConfiguration();
        SpelExpressionParser templateAwareExpressionParser = new SpelExpressionParser(spelParserConfiguration);
        SpelExpression spelExpression = templateAwareExpressionParser.parseRaw("${dto.name}");
        System.out.println(spelExpression.getValue());
    }

    public static void main2() {
        String expressionStr = "1 + 2";
        ExpressionParser parpser = new SpelExpressionParser(); //SpelExpressionParser是Spring内部对ExpressionParser的唯一最终实现类
        Expression exp = parpser.parseExpression(expressionStr); //把该表达式，解析成一个Expression对象：SpelExpression

        // 方式一：直接计算
        Object value = exp.getValue();
        System.out.println(value.toString()); //3
        // 若你在@Value中或者xml使用此表达式，请使用#{}包裹~~~~~~~~~~~~~~~~~
        System.out.println(parpser.parseExpression("T(System).getProperty('user.dir')").getValue()); //E:\work\remotegitcheckoutproject\myprojects\java\demo-war
        System.out.println(parpser.parseExpression("T(java.lang.Math).random() * 100.0").getValue()); //27.38227555400853

        // 方式二：定义环境变量，在环境内计算拿值
        // 环境变量可设置多个值：比如BeanFactoryResolver、PropertyAccessor、TypeLocator等~~~
        // 有环境变量，就有能力处理里面的占位符 ${}
        EvaluationContext context = new StandardEvaluationContext();
        System.out.println(exp.getValue(context)); //3
    }

    public static void main3() {
        String expressionStr = "#root";
        ExpressionParser parpser = new SpelExpressionParser(); //SpelExpressionParser是Spring内部对ExpressionParser的唯一最终实现类
        Expression exp = parpser.parseExpression(expressionStr); //把该表达式，解析成一个Expression对象：SpelExpression


        // 方式二：定义环境变量，在环境内计算拿值
        // 环境变量可设置多个值：比如BeanFactoryResolver、PropertyAccessor、TypeLocator等~~~
        // 有环境变量，就有能力处理里面的占位符 ${}
        Map<String,Object> map = new HashMap<>();
        map.put("a","123");
        map.put("b","bbbb");
        EvaluationContext context = new StandardEvaluationContext(map);
        System.out.println(exp.getValue(context)); //3
    }

    public static void main4() {
        String expressionStr = "#root.get('a')";
        ExpressionParser parpser = new SpelExpressionParser(); //SpelExpressionParser是Spring内部对ExpressionParser的唯一最终实现类
        Expression exp = parpser.parseExpression(expressionStr); //把该表达式，解析成一个Expression对象：SpelExpression


        // 方式二：定义环境变量，在环境内计算拿值
        // 环境变量可设置多个值：比如BeanFactoryResolver、PropertyAccessor、TypeLocator等~~~
        // 有环境变量，就有能力处理里面的占位符 ${}
        Map<String,Object> map = new HashMap<>();
        map.put("a","123");
        map.put("b","bbbb");
        EvaluationContext context = new StandardEvaluationContext(map);
        System.out.println(exp.getValue(context)); //3
    }

    public static void main5() {
        String expressionStr = "#root[0]";
        ExpressionParser parpser = new SpelExpressionParser(); //SpelExpressionParser是Spring内部对ExpressionParser的唯一最终实现类
        Expression exp = parpser.parseExpression(expressionStr); //把该表达式，解析成一个Expression对象：SpelExpression


        // 方式二：定义环境变量，在环境内计算拿值
        // 环境变量可设置多个值：比如BeanFactoryResolver、PropertyAccessor、TypeLocator等~~~
        // 有环境变量，就有能力处理里面的占位符 ${}
        Map<String,Object> map = new HashMap<>();
        map.put("a","123");
        map.put("b","bbbb");
        MethodInvocation invocation = new SimpleMethodInvocation(null,null,map);
        EvaluationContext context = new StandardEvaluationContext(invocation.getArguments());
        System.out.println(exp.getValue(context)); //3
    }

    public static void main6() throws Exception{
//        private Method method;
//        private Object[] arguments;
//        private Object targetObject;
//        String expressionStr = "#root.arguments[1]";
        String expressionStr = "#map";
        ExpressionParser parpser = new SpelExpressionParser(); //SpelExpressionParser是Spring内部对ExpressionParser的唯一最终实现类
        Expression exp = parpser.parseExpression(expressionStr); //把该表达式，解析成一个Expression对象：SpelExpression


        // 方式二：定义环境变量，在环境内计算拿值
        // 环境变量可设置多个值：比如BeanFactoryResolver、PropertyAccessor、TypeLocator等~~~
        // 有环境变量，就有能力处理里面的占位符 ${}
        Map<String,Object> map = new HashMap<>();
        map.put("a","123");
        map.put("b","bbbb");



        TestA testA = new TestA();
//        System.out.println(TestA.class.getMethods().length);
/*        Stream.of(TestA.class.getMethods()).forEach(obj -> {
            System.out.println("aa:  " + obj.getName());
            System.out.println(obj.getParameterTypes());
            Stream.of(obj.getParameterTypes()).forEach(obj1 -> {
                System.out.println(obj1.getName());
            });
        });*/
//        Method getTest = TestA.class.getMethod("getTest", Map.class, String.class);
//        System.out.println(getTest);
//        System.out.println(TestA.class.getDeclaredMethods()[0].getName());
        MethodInvocation methodInvocation = MethodInvocationUtils.create(testA, "getTest", map, "a");
        EvaluationContext context = new StandardEvaluationContext(methodInvocation);
        System.out.println(exp.getValue(context)); //3
    }

    public static void main(String[] args) throws Exception {
        main6();
    }

    public static class TestA {

        public String getTest(HashMap<String,Object> map,String a){
            return "";
        }
    }

}
