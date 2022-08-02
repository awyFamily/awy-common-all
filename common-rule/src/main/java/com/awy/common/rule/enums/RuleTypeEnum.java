package com.awy.common.rule.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 规则类型
 * @author yhw
 * @date 2022-08-02
 */
@AllArgsConstructor
public enum RuleTypeEnum {

    TIMER("时间(间隔时间)"),
    FIXED_NUMBER("固定次数(某段时间内)"),
    FLOAT_VALUE("浮动值"),
    ;

    public String getId() {
        return name();
    }

    @Getter
    private String description;

}
