package com.awy.common.rule.redis;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.awy.common.rule.IRule;
import com.awy.common.rule.RuleFactory;
import com.awy.common.rule.enums.RuleChainNodeTypeNum;
import com.awy.common.rule.enums.DefaultRuleTypeEnum;
import com.awy.common.rule.model.*;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * @author yhw
 * @date 2022-08-04
 */
public class RedisRuleFactory implements RuleFactory {

    private StringRedisTemplate redisTemplate;


    public RedisRuleFactory(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

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
                rule = new RedisTimerRule(ruleModel.getName(),ruleModel.getPriority(),ruleModel.getGroupName(),ruleChainNodeTypeNum,redisTemplate);
                break;
            case FIXED_NUMBER:
                rule = new RedisFixedNumberRule(ruleModel.getName(),ruleModel.getPriority(),ruleModel.getGroupName(),ruleChainNodeTypeNum,redisTemplate);
                break;
            case FLOAT_VALUE:
                rule = new RedisFloatValueRule(ruleModel.getName(),ruleModel.getPriority(),ruleModel.getGroupName(),ruleChainNodeTypeNum,redisTemplate);
                break;
            case SP_EL:
                rule = new RedisSpElRule(ruleModel.getName(),ruleModel.getPriority(),ruleModel.getGroupName(),ruleChainNodeTypeNum,redisTemplate);
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
