package com.awy.common.rule;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

import java.util.Map;

/**
 * @author yhw
 * @date 2022-08-01
 */
public abstract class ValueConditionRule extends AbstractRule {

    public ValueConditionRule(String name,int priority) {
        super(name,priority);
    }

    public abstract String getLastCondition(String key,String conditionKey);

    public abstract Boolean putLastCondition(String key,String conditionKey,String condition);

    public abstract Map<String,Float> getConditionMap();

    public abstract Boolean addCondition(String conditionKey,Float value);

    @Override
    public boolean isSupport(String key,String condition) {
        JSONObject json = JSONUtil.parseObj(condition);
        boolean result = false;
        Float conditionValue,lastConditionValue;
        String lastConditionStr;
        Map<String, Float> conditionMap = getConditionMap();
        for (String conditionKey : json.keySet()) {
            conditionValue = conditionMap.get(conditionKey);
            if (conditionValue != null && json.getFloat(conditionKey) != null) {
                lastConditionStr = getLastCondition(key,conditionKey);
                if (StrUtil.isNotBlank(lastConditionStr)) {
                    lastConditionValue = Float.valueOf(lastConditionStr);
                    //区间(last 上下浮动数值)
                    if (Math.abs(json.getFloat(conditionKey) - lastConditionValue) >= conditionValue) {
                        result = putLastCondition(key,conditionKey, json.getStr(conditionKey));
                    }
                } else {
                    result = putLastCondition(key,conditionKey, json.getStr(conditionKey));
                }
            }
        }
        return result;
    }
}
