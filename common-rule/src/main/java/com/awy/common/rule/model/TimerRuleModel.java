package com.awy.common.rule.model;

import cn.hutool.core.util.StrUtil;
import lombok.Data;

/**
 * @author yhw
 * @date 2022-08-04
 */
@Data
public class TimerRuleModel extends RuleConfigModel {

    private Long timeout = 60L;

    private String lastCachePrefix;

    public String getLastCachePrefix() {
        if (StrUtil.isNotBlank(this.lastCachePrefix)) {
            return this.lastCachePrefix;
        }
        return "last:timer:value";
    }
}
