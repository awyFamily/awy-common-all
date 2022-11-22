package com.awy.common.rule.redis;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.awy.common.rule.SpELRule;
import com.awy.common.rule.enums.RuleChainNodeTypeNum;
import com.awy.common.rule.model.SpElModel;
import com.awy.common.rule.model.SpElSimpleModel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yhw
 * @date 2022-08-16
 */
public class RedisSpElRule extends SpELRule {

    @Setter
    private StringRedisTemplate redisTemplate;

    @Setter
    @Getter
    private String conditionCacheKey = "condition:spel:key";

    public RedisSpElRule(String name, int priority, String groupName,StringRedisTemplate redisTemplate) {
        this(name, priority, groupName,RuleChainNodeTypeNum.fail_end,redisTemplate);
    }

    public RedisSpElRule(String name, int priority, String groupName, RuleChainNodeTypeNum typeNum,StringRedisTemplate redisTemplate) {
        super(name, priority, groupName,typeNum);
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Map<String, SpElSimpleModel> getConditionMap() {
        String conditionMapStr = this.redisTemplate.opsForValue().get(getConditionCacheKey());
        Map<String, SpElSimpleModel> result = new HashMap<>();
        if (StrUtil.isNotBlank(conditionMapStr)) {
            JSONObject json = JSONUtil.parseObj(conditionMapStr);
            for (String key : json.keySet()) {
                result.put(key,json.getBean(key,SpElSimpleModel.class));
            }
        }
        return result;
    }

    @Override
    public Boolean addCondition(SpElSimpleModel model) {
        Map<String, SpElSimpleModel> conditionMap = getConditionMap();
        conditionMap.put(model.getConditionKey(), model);
        this.redisTemplate.opsForValue().set(getConditionCacheKey(),JSONUtil.toJsonStr(conditionMap));
        return true;
    }

    @Override
    public void buildRuleConfig(SpElModel model) {
        super.buildRuleConfig(model);
        if (model != null && StrUtil.isNotBlank(model.getConditionCacheKey())) {
            setConditionCacheKey(model.getConditionCacheKey());
        }

    }
}
