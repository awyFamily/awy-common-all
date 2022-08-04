package com.awy.common.rule.redis;

import com.awy.common.rule.TimerRule;
import com.awy.common.rule.model.TimerRuleModel;
import lombok.Setter;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author yhw
 * @date 2022-08-02
 */
public class RedisTimerRule extends TimerRule<TimerRuleModel> {

    @Setter
    private StringRedisTemplate redisTemplate;


    public RedisTimerRule(String name, int priority,StringRedisTemplate redisTemplate) {
        super(name, priority);
        this.redisTemplate = redisTemplate;
    }

    public RedisTimerRule(String name, int priority, String groupName,StringRedisTemplate redisTemplate) {
        super(name, priority,groupName);
        this.redisTemplate = redisTemplate;
    }

    @Override
    public String getCache(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public Boolean putCache(String key) {
        redisTemplate.opsForValue().set(key,"1", getTimeout(), TimeUnit.SECONDS);
        return true;
    }

    @Override
    public void buildRuleConfig(TimerRuleModel timerRuleModel) {
        this.setTimeout(timerRuleModel.getTimeout());
    }
}
