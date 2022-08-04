package com.awy.common.rule.memory;

import cn.hutool.core.lang.Assert;
import com.awy.common.rule.IRule;
import com.awy.common.rule.RuleFactory;
import com.awy.common.rule.enums.RuleTypeEnum;
import com.awy.common.rule.model.*;

/**
 * @author yhw
 * @date 2022-08-03
 */
public class MemoryRuleFactory implements RuleFactory {

    @Override
    public IRule create(RuleModel ruleModel) {
        RuleTypeEnum ruleTypeEnum = RuleTypeEnum.valueOf(ruleModel.getRuleType());
        Assert.isFalse(ruleTypeEnum == null,"rule type not exists");
        IRule rule = null;
        RuleConfigModel model = null;
        switch (ruleTypeEnum) {
            case TIMER:
                model = ruleModel.getExpandBean(TimerRuleModel.class);
                rule = new MemoryTimerRule(ruleModel.getName(),ruleModel.getPriority(),ruleModel.getGroupName());
                break;
            case FIXED_NUMBER:
                model = ruleModel.getExpandBean(FixedNumberRuleModel.class);
                rule = new MemoryFixedNumberRule(ruleModel.getName(),ruleModel.getPriority(),ruleModel.getGroupName());
                break;
            case FLOAT_VALUE:
                model = ruleModel.getExpandBean(FloatValueRuleModel.class);
                rule = new MemoryFloatValueRule(ruleModel.getName(),ruleModel.getPriority(),ruleModel.getGroupName());
                break;
            default:
                break;
        }
        rule.buildRuleConfig(model);
        return rule;
    }

}
