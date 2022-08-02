package com.awy.common.rule.memory;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import com.awy.common.rule.TimerRule;

/**
 * @author yhw
 * @date 2022-08-01
 */
public class MemoryTimerRule extends TimerRule {

    private TimedCache<String,String> cache;

    public MemoryTimerRule(String name, int priority,long timeout) {
        super(name, priority);
        this.cache = CacheUtil.newTimedCache(timeout * 1000);
    }

    @Override
    public String getCache(String key) {
        return this.cache.get(key);
    }

    @Override
    public Boolean putCache(String key) {
        this.cache.put(key,"1");
        return true;
    }
}
