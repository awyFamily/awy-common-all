package com.awy.common.rule.model;

import lombok.Data;

/**
 * @author yhw
 * @date 2022-08-15
 */
@Data
public class SpElModel extends RuleConfigModel {

    /**
     * or
     * and
     */
    private String conCondition;

    private String conditionCacheKey;

    /**
     * {"cKey":"a","firstV":"b","firstE":"gt","conC":"or","secondV":"b","secondE":"lt"},
     * {"cKey":"a","firstV":"b","firstE":"gt","conC":"or","secondV":"b","secondE":"lt"}
     */
    private String els;
}
