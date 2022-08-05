package com.awy.common.rule.redis;

import com.awy.common.rule.TimerRule;
import com.awy.common.rule.enums.RuleChainNodeTypeNum;
import com.awy.common.rule.model.TimerRuleModel;
import lombok.Setter;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * @author yhw
 * @date 2022-08-02
 */
public class RedisTimerRule extends TimerRule<TimerRuleModel> {

    @Setter
    private StringRedisTemplate redisTemplate;

    public RedisTimerRule(String name, int priority,StringRedisTemplate redisTemplate) {
        this(name, priority,"default",RuleChainNodeTypeNum.fail_end,redisTemplate);
    }

    public RedisTimerRule(String name, int priority, String groupName,StringRedisTemplate redisTemplate) {
        this(name, priority,groupName,RuleChainNodeTypeNum.fail_end,redisTemplate);
    }

    public RedisTimerRule(String name, int priority, String groupName,RuleChainNodeTypeNum ruleChainNodeTypeNum, StringRedisTemplate redisTemplate) {
        super(name, priority,groupName,ruleChainNodeTypeNum);
        this.redisTemplate = redisTemplate;
    }

    @Override
    public String getCache(String key) {
        return redisTemplate.opsForValue().get(getLastCacheKey(key));
    }

    @Override
    public Boolean putCache(String key) {
        redisTemplate.opsForValue().set(getLastCacheKey(key),"1", getTimeout(), TimeUnit.SECONDS);
        return true;
    }

    @Override
    public void buildRuleConfig(TimerRuleModel model) {
        if (model == null) {
            return;
        }
        this.setTimeout(model.getTimeout());
        this.setLastCachePrefix(model.getLastCachePrefix());
    }
}
