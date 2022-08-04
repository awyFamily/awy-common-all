package com.awy.common.rule.memory;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.util.StrUtil;
import com.awy.common.rule.TimerRule;
import com.awy.common.rule.enums.RuleChainNodeTypeNum;
import com.awy.common.rule.model.TimerRuleModel;

/**
 * @author yhw
 * @date 2022-08-01
 */
public class MemoryTimerRule extends TimerRule<TimerRuleModel> {

    private TimedCache<String,String> cache;

    public MemoryTimerRule(String name, int priority) {
        this(name,priority,"default");
    }


    public MemoryTimerRule(String name, int priority,String groupName) {
        super(name, priority,groupName);
        this.cache = CacheUtil.newTimedCache(getTimeout() * 1000);
    }

    @Override
    public long getTimeout() {
        return super.getTimeout() * 1000;
    }

    @Override
    public String getCache(String key) {
        return this.cache.get(key,false);
    }

    @Override
    public Boolean putCache(String key) {
        this.cache.put(key,"1",getTimeout());
        return true;
    }

    @Override
    public void buildRuleConfig(TimerRuleModel model) {
        if (model == null) {
            return;
        }
        if (StrUtil.isNotBlank(model.getRuleChainNodeType())) {
            RuleChainNodeTypeNum ruleChainNodeTypeNum = RuleChainNodeTypeNum.valueOf(model.getRuleChainNodeType());
            setChainNodeTypeNum(ruleChainNodeTypeNum);
        }
        this.setTimeout(model.getTimeout());
    }
}
