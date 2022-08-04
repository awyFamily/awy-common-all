package com.awy.common.rule.memory;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import com.awy.common.rule.FixedNumberRule;
import com.awy.common.rule.model.FixedNumberRuleModel;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author yhw
 * @date 2022-08-02
 */
public class MemoryFixedNumberRule extends FixedNumberRule<FixedNumberRuleModel> {

    private TimedCache<String, AtomicInteger> map;

    private long timeout = 60;

    public MemoryFixedNumberRule(String name, int priority) {
        super(name, priority, 3);
        map = CacheUtil.newTimedCache(this.timeout * 1000);
    }

    public MemoryFixedNumberRule(String name, int priority, String groupName) {
        super(name, priority, 3,groupName);
        map = CacheUtil.newTimedCache(this.timeout * 1000);
    }

    @Override
    public int getNumber(String key) {
        AtomicInteger number = map.get(key,false);
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
            map.put(key,number, this.timeout * 1000);
        }
        number.incrementAndGet();
        return true;
    }

    @Override
    public void buildRuleConfig(FixedNumberRuleModel model) {
        this.timeout = model.getTimeout();
        setFixedNumber(model.getFixedNumber());
    }
}
