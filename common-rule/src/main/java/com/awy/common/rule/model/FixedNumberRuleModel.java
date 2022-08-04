package com.awy.common.rule.model;

import lombok.Data;

/**
 * @author yhw
 * @date 2022-08-04
 */
@Data
public class FixedNumberRuleModel extends RuleConfigModel {

    private Long timeout = 60L;

    private Integer fixedNumber = 3;
}
