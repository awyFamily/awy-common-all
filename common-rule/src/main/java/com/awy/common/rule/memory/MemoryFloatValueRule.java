package com.awy.common.rule.memory;

import com.awy.common.rule.FloatValueRule;
import com.awy.common.rule.enums.RuleChainNodeTypeNum;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yhw
 * @date 2022-08-02
 */
public class MemoryFloatValueRule extends FloatValueRule {

    private Map<String,Number> conditionRepository;

    private Map<String,String> lastConditionMap;

    public MemoryFloatValueRule(String name, int priority) {
        this(name, priority,"default");
    }

    public MemoryFloatValueRule(String name, int priority,String groupName) {
        this(name, priority,groupName,RuleChainNodeTypeNum.fail_end);
    }

    public MemoryFloatValueRule(String name, int priority, String groupName, RuleChainNodeTypeNum ruleChainNodeTypeNum) {
        super(name, priority,groupName,ruleChainNodeTypeNum);
        this.conditionRepository = new ConcurrentHashMap<>();
        this.lastConditionMap = new ConcurrentHashMap<>();
    }

    @Override
    public String getLastCondition(String key,String conditionKey) {
        return lastConditionMap.get(getLastCacheKey(key) + conditionKey);
    }

    @Override
    public Boolean putLastCondition(String key,String conditionKey, String condition) {
        lastConditionMap.put(getLastCacheKey(key) + conditionKey,condition);
        return true;
    }

    @Override
    public Map<String, Number> getConditionMap() {
        return this.conditionRepository;
    }

    @Override
    public Boolean addCondition(String conditionKey, Float value) {
        this.conditionRepository.put(conditionKey,value);
        return true;
    }

}
