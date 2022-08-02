package com.awy.common.rule.memory;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import com.awy.common.rule.FixedNumberRule;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author yhw
 * @date 2022-08-02
 */
public class MemoryFixedNumberRule extends FixedNumberRule {

    private TimedCache<String, AtomicInteger> map;

    public MemoryFixedNumberRule(String name, int priority, int fixedNumber,long timeout) {
        super(name, priority, fixedNumber);
        map = CacheUtil.newTimedCache(timeout * 1000);
    }

    @Override
    public int getNumber(String key) {
        AtomicInteger number = map.get(key);
        if (number == null) {
            number = new AtomicInteger(0);
        }
        return number.get();
    }

    @Override
    public Boolean increment(String key) {
        AtomicInteger number = map.get(key);
        if (number == null) {
            number = new AtomicInteger(0);
            map.put(key,number);
        }
        number.incrementAndGet();
        return true;
    }
}
