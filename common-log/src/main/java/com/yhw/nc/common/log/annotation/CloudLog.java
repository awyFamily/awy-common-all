package com.yhw.nc.common.log.annotation;

import com.yhw.nc.common.log.enums.LogTypeEnum;

import java.lang.annotation.*;

/**
 * 日志注解
 * @author yhw
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CloudLog {

    /**
     * 日志类型
     * @return
     */
    LogTypeEnum logType() default LogTypeEnum.STANDARD;

    /**
     * 日志备注 默认为空
     * @return str
     */
    String remark() default "";

}
