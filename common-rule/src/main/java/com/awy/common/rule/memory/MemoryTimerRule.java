package com.awy.common.rule.memory;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import com.awy.common.rule.TimerRule;
import com.awy.common.rule.model.TimerRuleModel;

/**
 * @author yhw
 * @date 2022-08-01
 */
public class MemoryTimerRule extends TimerRule<TimerRuleModel> {

    private TimedCache<String,String> cache;

    public MemoryTimerRule(String name, int priority) {
        this(name,priority,60);
    }

    public MemoryTimerRule(String name, int priority,long timeout) {
        super(name, priority,timeout);
        this.cache = CacheUtil.newTimedCache(timeout * 1000);
    }

    public MemoryTimerRule(String name, int priority,String groupName) {
        super(name, priority,groupName,60);
        this.cache = CacheUtil.newTimedCache(getTimeout() * 1000);
    }

    @Override
    public long getTimeout() {
        return super.getTimeout() * 1000;
    }

    @Override
    public String getCache(String key) {
        return this.cache.get(key);
    }

    @Override
    public Boolean putCache(String key) {
        this.cache.put(key,"1",getTimeout());
        return true;
    }

    @Override
    public void buildRuleConfig(TimerRuleModel timerRuleModel) {
        this.setTimeout(timerRuleModel.getTimeout());
    }
}
