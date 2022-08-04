package com.awy.common.rule;

import com.awy.common.rule.enums.RuleChainNodeTypeNum;
import com.awy.common.rule.enums.RuleTypeEnum;
import com.awy.common.rule.model.RuleConfigModel;

/**
 * @author yhw
 * @date 2022-08-01
 */
public interface IRule<T extends RuleConfigModel> {

    RuleTypeEnum getType();

    String getName();

    String getGroupName();

    RuleChainNodeTypeNum getChainNodeType();

    boolean isSupport(String key,String condition);

    boolean successCallback(String key,String condition);

    boolean handler(String key,String condition);

    int getPriority();

    void buildRuleConfig(T model);

}
