package com.awy.common.rule.memory;

import com.awy.common.rule.FloatValueRule;
import com.awy.common.rule.model.FloatValueRuleModel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yhw
 * @date 2022-08-02
 */
public class MemoryFloatValueRule extends FloatValueRule<FloatValueRuleModel> {

    private Map<String,Float> conditionRepository;

    private Map<String,String> lastConditionMap;

    public MemoryFloatValueRule(String name, int priority) {
        super(name, priority);
        this.conditionRepository = new ConcurrentHashMap<>();
        this.lastConditionMap = new ConcurrentHashMap<>();
    }

    public MemoryFloatValueRule(String name, int priority,String groupName) {
        super(name, priority,groupName);
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
    public Map<String, Float> getConditionMap() {
        return this.conditionRepository;
    }

    @Override
    public Boolean addCondition(String conditionKey, Float value) {
        this.conditionRepository.put(conditionKey,value);
        return true;
    }

    @Override
    public void buildRuleConfig(FloatValueRuleModel model) {
        setLastCachePrefix(model.getLastCachePrefix());
        setConditionCacheKey(model.getConditionCacheKey());
    }
}
