package com.awy.common.rule.memory;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.awy.common.rule.IRule;
import com.awy.common.rule.RuleFactory;
import com.awy.common.rule.enums.DefaultRuleTypeEnum;
import com.awy.common.rule.enums.RuleChainNodeTypeNum;
import com.awy.common.rule.model.RuleConfigModel;
import com.awy.common.rule.model.RuleModel;

/**
 * @author yhw
 * @date 2022-08-03
 */
public class MemoryRuleFactory implements RuleFactory {

    @Override
    public IRule create(RuleModel ruleModel) {
        DefaultRuleTypeEnum ruleTypeEnum = DefaultRuleTypeEnum.valueOf(ruleModel.getRuleType());
        Assert.isFalse(ruleTypeEnum == null,"rule type not exists");
        RuleChainNodeTypeNum ruleChainNodeTypeNum = RuleChainNodeTypeNum.fail_end;
        if (StrUtil.isNotBlank(ruleModel.getRuleChainNodeType())) {
            ruleChainNodeTypeNum = RuleChainNodeTypeNum.valueOf(ruleModel.getRuleChainNodeType());
            Assert.isFalse(ruleChainNodeTypeNum == null,"rule chain type not exists");
        }
        IRule rule = null;
        switch (ruleTypeEnum) {
            case TIMER:
                rule = new MemoryTimerRule(ruleModel.getName(),ruleModel.getPriority(),ruleModel.getGroupName(),ruleChainNodeTypeNum);
                break;
            case FIXED_NUMBER:
                rule = new MemoryFixedNumberRule(ruleModel.getName(),ruleModel.getPriority(),ruleModel.getGroupName(),ruleChainNodeTypeNum);
                break;
            case FLOAT_VALUE:
                rule = new MemoryFloatValueRule(ruleModel.getName(),ruleModel.getPriority(),ruleModel.getGroupName(),ruleChainNodeTypeNum);
                break;
            case SP_EL:
                rule = new MemorySpElRule(ruleModel.getName(),ruleModel.getPriority(),ruleModel.getGroupName(),ruleChainNodeTypeNum);
                break;
            default:
                break;
        }
        if (rule != null) {
            RuleConfigModel model = ruleModel.getExpandBean(ruleTypeEnum.getConfModelClass());
            rule.buildRuleConfig(model);
        }
        return rule;
    }

}
