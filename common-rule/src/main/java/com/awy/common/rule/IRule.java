package com.awy.common.rule;

/**
 * @author yhw
 * @date 2022-08-01
 */
public interface IRule {

    String getName();

    boolean isSupport(String key,String condition);

    int getPriority();

}
