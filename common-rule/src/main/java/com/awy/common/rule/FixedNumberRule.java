package com.awy.common.rule;

import com.awy.common.rule.enums.RuleChainNodeTypeNum;
import com.awy.common.rule.enums.DefaultRuleTypeEnum;
import com.awy.common.rule.model.RuleConfigModel;
import lombok.Setter;

/**
 * @author yhw
 * @date 2022-08-02
 */
public abstract class FixedNumberRule<T extends RuleConfigModel> extends AbstractRule<T> {

    @Setter
    private int fixedNumber;

    public FixedNumberRule(String name,int priority,int fixedNumber) {
        this(name,priority,fixedNumber,"default");
    }

    public FixedNumberRule(String name,int priority,int fixedNumber, String groupName) {
        super(name,priority,groupName);
        this.fixedNumber = fixedNumber;
    }

    public FixedNumberRule(String name, int priority, int fixedNumber, String groupName, RuleChainNodeTypeNum ruleChainNodeTypeNum) {
        super(name,priority,groupName,ruleChainNodeTypeNum);
        this.fixedNumber = fixedNumber;
    }

    @Override
    public String getType() {
        return DefaultRuleTypeEnum.FIXED_NUMBER.getId();
    }

    public abstract int getNumber(String key);

    public abstract Boolean increment(String key);

    /**
     * 符合条件，并更新缓存
     * @param key 缓存key
     * @param condition 值内容
     * @return 布尔值
     */
    @Override
    public boolean isSupport(String key,String condition) {
        return getNumber(key) < this.fixedNumber;
    }

    @Override
    public boolean successCallback(String key, String condition) {
        return increment(key);
    }

    @Override
    public boolean handler(String key, String condition) {
        if(isSupport(key, condition)) {
            return successCallback(key, condition);
        }
        return false;
    }
}