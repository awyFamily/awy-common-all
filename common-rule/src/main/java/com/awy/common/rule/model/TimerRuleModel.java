package com.awy.common.rule.model;

import lombok.Data;

/**
 * @author yhw
 * @date 2022-08-04
 */
@Data
public class TimerRuleModel extends RuleConfigModel {

    private Long timeout = 60L;


}
