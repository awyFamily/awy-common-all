package com.awy.common.rule.model;

import lombok.Data;

/**
 * @author yhw
 * @date 2022-08-04
 */
@Data
public class FloatValueRuleModel extends RuleConfigModel {

    private String lastCachePrefix;
    private String conditionCacheKey;
    /**
     * {
     *    "a": "3.6",
     *    "b"："3.7"
     * }
     */
    private String floatValueMaps;

}
