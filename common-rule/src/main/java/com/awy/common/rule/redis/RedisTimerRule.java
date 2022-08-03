package com.awy.common.rule.redis;

import com.awy.common.rule.TimerRule;
import lombok.Setter;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author yhw
 * @date 2022-08-02
 */
public class RedisTimerRule extends TimerRule {

    @Setter
    private StringRedisTemplate redisTemplate;
    private long timeout;


    public RedisTimerRule(String name, int priority, long timeout) {
        super(name, priority);
        this.timeout = timeout;
    }

    public RedisTimerRule(String name, int priority, long timeout,StringRedisTemplate redisTemplate) {
        super(name, priority);
        this.timeout = timeout;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public String getCache(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public Boolean putCache(String key) {
        redisTemplate.opsForValue().set(key,"1", timeout, TimeUnit.SECONDS);
        return true;
    }
}
