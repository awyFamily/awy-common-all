package com.awy.common.rule;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.awy.common.rule.enums.RuleChainNodeTypeNum;
import com.awy.common.rule.enums.DefaultRuleTypeEnum;
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
    private String lastCachePrefix = "last:float:value";
    @Setter
    @Getter
    private String conditionCacheKey = "condition:cache:key";

    public FloatValueRule(String name, int priority) {
        this(name,priority,"default");
    }

    public FloatValueRule(String name, int priority,String groupName) {
        super(name,priority,groupName);
    }

    public FloatValueRule(String name, int priority,String groupName,RuleChainNodeTypeNum ruleChainNodeTypeNum) {
        super(name,priority,groupName,ruleChainNodeTypeNum);
    }

    public String getLastCacheKey(String key) {
        return lastCachePrefix.concat(":").concat(key);
    }

    @Override
    public String getType() {
        return DefaultRuleTypeEnum.FLOAT_VALUE.getId();
    }

    public abstract String getLastCondition(String key, String conditionKey);

    public abstract Boolean putLastCondition(String key,String conditionKey,String condition);

    public abstract Map<String,Number> getConditionMap();

    public abstract Boolean addCondition(String conditionKey,Float value);

    @Override
    public boolean isSupport(String key,String condition) {
        if (!JSONUtil.isTypeJSON(condition)) {
            return false;
        }
        JSONObject json = JSONUtil.parseObj(condition);
        Number conditionValue;
        Float lastConditionValue;
        String lastConditionStr;
        Map<String, Number> conditionMap = getConditionMap();
        //只要满足其中一个(或为第一次触发)，则返回 true
        for (String conditionKey : json.keySet()) {
            conditionValue = conditionMap.get(conditionKey);
            if (conditionValue != null && json.getFloat(conditionKey) != null) {
                lastConditionStr = getLastCondition(key,conditionKey);
                if (StrUtil.isNotBlank(lastConditionStr)) {
                    lastConditionValue = Float.valueOf(lastConditionStr);
                    //区间(last 上下浮动数值)
                    if (Math.abs(json.getFloat(conditionKey) - lastConditionValue) >= conditionValue.floatValue()) {
                        return true;
                    }
                } else {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean successCallback(String key, String condition) {
        JSONObject json = JSONUtil.parseObj(condition);
        Number conditionValue;
        Float lastConditionValue;
        String lastConditionStr;
        Map<String, Number> conditionMap = getConditionMap();
        for (String conditionKey : json.keySet()) {
            conditionValue = conditionMap.get(conditionKey);
            if (conditionValue != null && json.getFloat(conditionKey) != null) {
                lastConditionStr = getLastCondition(key,conditionKey);
                if (StrUtil.isNotBlank(lastConditionStr)) {
                    lastConditionValue = Float.valueOf(lastConditionStr);
                    //区间(last 上下浮动数值)
                    if (Math.abs(json.getFloat(conditionKey) - lastConditionValue) >= conditionValue.floatValue()) {
                        putLastCondition(key,conditionKey, json.getStr(conditionKey));
                    }
                } else {
                    putLastCondition(key,conditionKey, json.getStr(conditionKey));
                }
            }
        }
        return true;
    }

    @Override
    public boolean handler(String key, String condition) {
        if(isSupport(key, condition)) {
            return successCallback(key, condition);
        }
        return false;
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
