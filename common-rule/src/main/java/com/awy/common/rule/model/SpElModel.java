package com.awy.common.rule.model;

import lombok.Data;

/**
 * @author yhw
 * @date 2022-08-15
 */
@Data
public class SpElModel extends RuleConfigModel {

    /**
     * false  = or
     * true = and
     */
    private Boolean hasAndConCondition = false;

    private String conditionCacheKey;

    /**
     * [{"cKey":"a","firstV":"b","firstE":"gt","conC":"or","secondV":"b","secondE":"lt"},
     * {"cKey":"a","firstV":"b","firstE":"gt","conC":"or","secondV":"b","secondE":"lt"}]
     */
    private String els;
}
