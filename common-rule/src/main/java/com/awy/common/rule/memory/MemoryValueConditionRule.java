package com.awy.common.rule.memory;

import com.awy.common.rule.ValueConditionRule;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yhw
 * @date 2022-08-02
 */
public class MemoryValueConditionRule extends ValueConditionRule {

    private Map<String,Float> conditionRepository;

    private Map<String,String> lastConditionMap;

    public MemoryValueConditionRule(String name, int priority) {
        super(name, priority);
        this.conditionRepository = new ConcurrentHashMap<>();
        this.lastConditionMap = new ConcurrentHashMap<>();
    }

    @Override
    public String getLastCondition(String key,String conditionKey) {
        return lastConditionMap.get(key + conditionKey);
    }

    @Override
    public Boolean putLastCondition(String key,String conditionKey, String condition) {
        lastConditionMap.put(key + conditionKey,condition);
        return true;
    }

    @Override
    public Map<String, Float> getConditionMap() {
        return this.conditionRepository;
    }

    @Override
    public Boolean addCondition(String conditionKey, Float value) {
        this.conditionRepository.put(conditionKey,value);
        return true;
    }
}
