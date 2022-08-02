package com.awy.common.rule.redis;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.awy.common.rule.ValueConditionRule;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yhw
 * @date 2022-08-02
 */
public class RedisValueConditionRule extends ValueConditionRule {

    private StringRedisTemplate redisTemplate;

    private String lastCachePrefix;
    private String conditionCacheKey;

    public RedisValueConditionRule(String name, int priority,String lastCachePrefix,String conditionCacheKey,StringRedisTemplate redisTemplate) {
        super(name, priority);
        this.lastCachePrefix = lastCachePrefix;
        this.conditionCacheKey = conditionCacheKey;
    }

    private String getLastCacheKey(String key) {
        return lastCachePrefix.concat(":").concat(key);
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
        String conditionMapStr = this.redisTemplate.opsForValue().get(conditionCacheKey);
        if (StrUtil.isNotBlank(conditionMapStr)) {
            return JSONUtil.toBean(conditionMapStr,HashMap.class);
        }
        return new HashMap<>();
    }

    @Override
    public Boolean addCondition(String conditionKey, Float value) {
        Map<String, Float> conditionMap = getConditionMap();
        conditionMap.put(conditionKey, value);
        this.redisTemplate.opsForValue().set(conditionCacheKey,JSONUtil.toJsonStr(conditionMap));
        return true;
    }
}
