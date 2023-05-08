package com.awy.common.rule.memory;

import com.awy.common.rule.SpELRule;
import com.awy.common.rule.enums.RuleChainNodeTypeNum;
import com.awy.common.rule.model.SpElSimpleModel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yhw
 * @date 2022-08-16
 */
public class MemorySpElRule extends SpELRule {

    private Map<String,SpElSimpleModel> conditionRepository;

    public MemorySpElRule(String name, int priority, String groupName) {
        this(name, priority, groupName,RuleChainNodeTypeNum.fail_end);
    }

    public MemorySpElRule(String name, int priority, String groupName, RuleChainNodeTypeNum ruleChainNodeTypeNum) {
        super(name, priority, groupName, ruleChainNodeTypeNum);
        this.conditionRepository = new ConcurrentHashMap<>();
    }

    @Override
    public Map<String, SpElSimpleModel> getConditionMap() {
        return this.conditionRepository;
    }

    @Override
    public Boolean addCondition(SpElSimpleModel model) {
        this.conditionRepository.put(model.getConditionKey(),model);
        return true;
    }
}
