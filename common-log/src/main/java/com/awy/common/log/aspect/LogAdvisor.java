package com.awy.common.log.aspect;

import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.aop.support.ComposablePointcut;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.util.Assert;

import java.lang.annotation.Annotation;

/**
 * 切入点(通用实现)
 * reference：https://www.jianshu.com/p/9bb149b39457
 * @author yhw
 */
public class LogAdvisor extends AbstractPointcutAdvisor  implements BeanFactoryAware {

    private Advice advice;
    private Pointcut pointcut;

    /**
     * 构造切入点
     * @param annotationType 自定义注解
     * @param interceptor MethodInterceptor实现
     */
    public LogAdvisor (Class<? extends Annotation> annotationType, MethodInterceptor interceptor){
        this.advice = interceptor;
        this.pointcut = buildPointcut(annotationType);
    }

    @Override
    public Pointcut getPointcut() {
        return this.pointcut;
    }

    @Override
    public Advice getAdvice() {
        return this.advice;
    }

    private Pointcut buildPointcut(Class<? extends Annotation> annotationType) {
        Assert.notNull(annotationType, "'annotationTypes' must not be null");
        ComposablePointcut result = null;
        //类级别(true=检查超类和接口)
        Pointcut cpc = new AnnotationMatchingPointcut(annotationType, true);
        //方法级别
        Pointcut mpc = AnnotationMatchingPointcut.forMethodAnnotation(annotationType);
        //对于类和方法上都可以添加注解的情况
        //类上的注解,最终会将注解绑定到每个方法上
        if (result == null) {
            result = new ComposablePointcut(cpc);
        }
        return result.union(mpc);
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        if (this.advice instanceof BeanFactoryAware) {
            ((BeanFactoryAware) this.advice).setBeanFactory(beanFactory);
        }
    }
}
