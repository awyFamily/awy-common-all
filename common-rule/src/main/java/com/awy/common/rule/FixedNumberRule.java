package com.awy.common.rule;

import com.awy.common.rule.enums.RuleTypeEnum;
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

    @Override
    public RuleTypeEnum getType() {
        return RuleTypeEnum.FIXED_NUMBER;
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
        if (getNumber(key) >= this.fixedNumber) {
            return false;
        }
        return increment(key);
    }

}
