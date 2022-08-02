package com.awy.common.rule;

import cn.hutool.core.util.StrUtil;
import com.awy.common.rule.enums.RuleTypeEnum;

/**
 * @author yhw
 * @date 2022-08-01
 */
public abstract class TimerRule extends AbstractRule {

    public TimerRule(String name, int priority) {
        this(name,priority,"default");
    }

    public TimerRule(String name, int priority, String groupName) {
        super(name,priority,groupName);
    }

    @Override
    public RuleTypeEnum getType() {
        return RuleTypeEnum.TIMER;
    }

    public abstract String getCache(String key);

    public abstract Boolean putCache(String key);

    /**
     * 符合条件，并更新缓存
     * @param key 缓存key
     * @param condition 值内容
     * @return 布尔值
     */
    @Override
    public boolean isSupport(String key,String condition) {
        if (StrUtil.isNotBlank(getCache(key))) {
            return false;
        }
        return putCache(key);
    }

}
