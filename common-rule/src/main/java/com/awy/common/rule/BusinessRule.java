package com.awy.common.rule;

import com.awy.common.rule.enums.DefaultRuleTypeEnum;
import com.awy.common.rule.enums.RuleChainNodeTypeNum;
import com.awy.common.rule.model.BusinessModel;

/**
 * @author yhw
 * @date 2022-08-19
 */
public abstract class BusinessRule extends AbstractRule<BusinessModel>  {

    public BusinessRule(String name, int priority, String groupName) {
        this(name, priority, groupName,RuleChainNodeTypeNum.fail_end);
    }

    public BusinessRule(String name, int priority, String groupName, RuleChainNodeTypeNum ruleChainNodeTypeNum) {
        super(name, priority, groupName, ruleChainNodeTypeNum);
    }

    @Override
    public String getType() {
        return DefaultRuleTypeEnum.BUSINESS.getId();
    }

    @Override
    public boolean handler(String key, String condition) {
        if(isSupport(key, condition)) {
            return successCallback(key, condition);
        }
        return false;
    }

}
