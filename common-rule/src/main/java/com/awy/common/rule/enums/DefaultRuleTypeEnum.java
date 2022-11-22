package com.awy.common.rule.enums;

import com.awy.common.rule.model.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 规则类型
 * @author yhw
 * @date 2022-08-02
 */
@AllArgsConstructor
public enum DefaultRuleTypeEnum {

    TIMER("时间(间隔时间)", TimerRuleModel.class),
    FIXED_NUMBER("固定次数(某段时间内)",FixedNumberRuleModel.class),
    FLOAT_VALUE("浮动值", FloatValueRuleModel.class),
    SP_EL("EL表达式", SpElModel.class),
    BUSINESS("自定义业务", BusinessModel.class),
    ;

    public String getId() {
        return name();
    }

    @Getter
    private String description;

    @Getter
    private Class<? extends RuleConfigModel> confModelClass;

}
