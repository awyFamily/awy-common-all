package com.awy.common.rule.redis;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.util.StrUtil;
import com.awy.common.rule.FixedNumberRule;
import lombok.Setter;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author yhw
 * @date 2022-08-02
 */
public class RedisFixedNumberRule extends FixedNumberRule {

    @Setter
    private StringRedisTemplate redisTemplate;
    private long timeout;

    public RedisFixedNumberRule(String name, int priority, int fixedNumber, long timeout, StringRedisTemplate redisTemplate) {
        super(name, priority, fixedNumber);
        this.timeout = timeout;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public int getNumber(String key) {
        String str = redisTemplate.opsForValue().get(key);
        return StrUtil.isBlank(str) ? 0 : Integer.valueOf(str);
    }

    @Override
    public Boolean increment(String key) {
        String str = redisTemplate.opsForValue().get(key);
        if (StrUtil.isNotBlank(str)) {
            int number = Integer.valueOf(str);
            number++;
            redisTemplate.opsForValue().set(key,number + "",redisTemplate.getExpire(key, TimeUnit.SECONDS));
            return true;
        }
        redisTemplate.opsForValue().set(key,"1",timeout,TimeUnit.SECONDS);
        return true;
    }
}
