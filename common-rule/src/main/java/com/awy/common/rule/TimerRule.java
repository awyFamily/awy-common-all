package com.awy.common.rule;

import cn.hutool.core.util.StrUtil;
import com.awy.common.rule.enums.RuleChainNodeTypeNum;
import com.awy.common.rule.enums.RuleTypeEnum;
import com.awy.common.rule.model.RuleConfigModel;
import lombok.Getter;
import lombok.Setter;

/**
 * @author yhw
 * @date 2022-08-01
 */
public abstract class TimerRule<T extends RuleConfigModel> extends AbstractRule<T> {

    @Getter
    @Setter
    private long timeout;

    public TimerRule(String name, int priority) {
        this(name,priority,"default");
    }

    public TimerRule(String name, int priority, String groupName) {
        super(name,priority,groupName);
    }

    public TimerRule(String name, int priority, String groupName, RuleChainNodeTypeNum ruleChainNodeTypeNum) {
        super(name,priority,groupName,ruleChainNodeTypeNum);
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
        return StrUtil.isBlank(getCache(key));
    }

    @Override
    public boolean successCallback(String key, String condition) {
        return putCache(key);
    }

    @Override
    public boolean handler(String key, String condition) {
        if(isSupport(key, condition)) {
            return successCallback(key, condition);
        }
        return false;
    }

}
