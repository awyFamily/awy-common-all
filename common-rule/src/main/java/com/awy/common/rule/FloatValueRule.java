package com.awy.common.rule;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.awy.common.rule.enums.RuleTypeEnum;
import com.awy.common.rule.model.FloatValueRuleModel;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * 浮动
 * @author yhw
 * @date 2022-08-01
 */
public abstract class FloatValueRule extends AbstractRule<FloatValueRuleModel> {

    @Setter
    private String lastCachePrefix;
    @Setter
    @Getter
    private String conditionCacheKey;

    public FloatValueRule(String name, int priority) {
        this(name,priority,"default");
    }

    public FloatValueRule(String name, int priority,String groupName) {
        this(name,priority,groupName,"last:float:value","condition:cache:key");
    }

    public FloatValueRule(String name, int priority,String lastCachePrefix, String conditionCacheKey) {
        this(name,priority,"default",lastCachePrefix,conditionCacheKey);
    }

    public FloatValueRule(String name, int priority, String groupName,String lastCachePrefix, String conditionCacheKey) {
        super(name,priority,groupName);
        this.lastCachePrefix = lastCachePrefix;
        this.conditionCacheKey = conditionCacheKey;
    }

    public String getLastCacheKey(String key) {
        return lastCachePrefix.concat(":").concat(key);
    }

    @Override
    public RuleTypeEnum getType() {
        return RuleTypeEnum.FLOAT_VALUE;
    }

    public abstract String getLastCondition(String key, String conditionKey);

    public abstract Boolean putLastCondition(String key,String conditionKey,String condition);

    public abstract Map<String,Float> getConditionMap();

    public abstract Boolean addCondition(String conditionKey,Float value);

    @Override
    public boolean isSupport(String key,String condition) {
        if (!JSONUtil.isTypeJSON(condition)) {
            return false;
        }
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

    @Override
    public void buildRuleConfig(FloatValueRuleModel model) {
        if (model == null) {
            return;
        }
        if (StrUtil.isNotBlank(model.getLastCachePrefix())) {
            setLastCachePrefix(model.getLastCachePrefix());
        }
        if (StrUtil.isNotBlank(model.getConditionCacheKey())) {
            setConditionCacheKey(model.getConditionCacheKey());
        }
        if (StrUtil.isNotBlank(model.getFloatValueMaps())) {
            JSONObject floatValueMaps = JSONUtil.parseObj(model.getFloatValueMaps());
            for (String key : floatValueMaps.keySet()) {
                addCondition(key,floatValueMaps.getFloat(key));
            }
        }
    }
}
