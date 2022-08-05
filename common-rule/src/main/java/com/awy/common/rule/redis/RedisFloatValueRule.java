package com.awy.common.rule.redis;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.awy.common.rule.FloatValueRule;
import com.awy.common.rule.enums.RuleChainNodeTypeNum;
import lombok.Setter;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yhw
 * @date 2022-08-02
 */
public class RedisFloatValueRule extends FloatValueRule {

    @Setter
    private StringRedisTemplate redisTemplate;

    public RedisFloatValueRule(String name, int priority,StringRedisTemplate redisTemplate) {
        this(name,priority,"default",redisTemplate);
    }

    public RedisFloatValueRule(String name, int priority, String groupName,StringRedisTemplate redisTemplate) {
        this(name, priority,groupName,RuleChainNodeTypeNum.fail_end,redisTemplate);
    }

    public RedisFloatValueRule(String name, int priority, String groupName, RuleChainNodeTypeNum ruleChainNodeTypeNum, StringRedisTemplate redisTemplate) {
        super(name, priority,groupName,ruleChainNodeTypeNum);
        this.redisTemplate = redisTemplate;
    }


    @Override
    public String getLastCondition(String key,String conditionKey) {
        return (String)redisTemplate.opsForHash().get(getLastCacheKey(key),conditionKey);
    }

    @Override
    public Boolean putLastCondition(String key,String conditionKey, String condition) {
        redisTemplate.opsForHash().put(getLastCacheKey(key),conditionKey,condition);
        return true;
    }

    @Override
    public Map<String, Float> getConditionMap() {
        String conditionMapStr = this.redisTemplate.opsForValue().get(getConditionCacheKey());
        if (StrUtil.isNotBlank(conditionMapStr)) {
            return JSONUtil.toBean(conditionMapStr,HashMap.class);
        }
        return new HashMap<>();
    }

    @Override
    public Boolean addCondition(String conditionKey, Float value) {
        Map<String, Float> conditionMap = getConditionMap();
        conditionMap.put(conditionKey, value);
        this.redisTemplate.opsForValue().set(getConditionCacheKey(),JSONUtil.toJsonStr(conditionMap));
        return true;
    }

}
