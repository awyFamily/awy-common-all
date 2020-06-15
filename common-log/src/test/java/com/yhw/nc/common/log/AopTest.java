package com.yhw.nc.common.log;

import com.yhw.nc.common.log.annotation.CloudLog;
import com.yhw.nc.common.log.aspect.LogAdvisor;
import com.yhw.nc.common.log.aspect.LogInterceptor;

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

    public static void main(String[] args) {
        AopTest test = new AopTest();
        test.test1();
        test.test2();

        LogAdvisor advisor = new LogAdvisor(CloudLog.class, new LogInterceptor(null));
        test = new AopTest();
        test.test1();
        test.test2();
    }

}
