package com.awy.common.rule.model;

import cn.hutool.core.util.StrUtil;
import lombok.Data;

/**
 * @author yhw
 * @date 2022-08-04
 */
@Data
public class TimerRuleModel extends RuleConfigModel {

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

    private String lastCachePrefix;

    public String getLastCachePrefix() {
        if (StrUtil.isNotBlank(this.lastCachePrefix)) {
            return this.lastCachePrefix;
        }
        return "last:timer:value";
    }
}
