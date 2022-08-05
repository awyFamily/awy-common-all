package com.awy.common.rule.model;

import lombok.Data;

/**
 * @author yhw
 * @date 2022-08-04
 */
@Data
public class FixedNumberRuleModel extends RuleConfigModel {

    /**
     *
     * seconds
     * minutes
     * hours
     * days
     */
    private String timeUnit = "seconds";

    private Long timeout = 60L;

    public Long getTimeout() {
        if ("seconds".equalsIgnoreCase(this.timeUnit)) {
            return this.timeout;
        } else if ("minutes".equalsIgnoreCase(this.timeUnit)) {
            return this.timeout * 60;
        } else if ("hours".equalsIgnoreCase(this.timeUnit)) {
            return this.timeout * 60 * 60;
        } else if ("days".equalsIgnoreCase(this.timeUnit)) {
            return this.timeout * 60 * 60 * 24;
        }
        return this.timeout;
    }

    private Integer fixedNumber = 3;

}
