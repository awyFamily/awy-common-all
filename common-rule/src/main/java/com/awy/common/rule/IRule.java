package com.awy.common.rule;

import com.awy.common.rule.enums.RuleTypeEnum;

/**
 * @author yhw
 * @date 2022-08-01
 */
public interface IRule {

    RuleTypeEnum getType();

    String getName();

    String getGroupName();

    boolean isSupport(String key,String condition);

    int getPriority();

}
