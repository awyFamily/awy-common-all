package com.awy.common.rule.model;

import cn.hutool.core.text.StrBuilder;
import cn.hutool.core.util.StrUtil;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yhw
 * @date 2022-08-15
 */
@Data
@NoArgsConstructor
public class SpElSimpleModel {

    private String cKey;

    public String getConditionKey() {
        return this.cKey;
    }

    private String firstV;

    public String getFirstValue() {
        return firstV;
    }

    private String firstE;

    public String getFirstExpression() {
        return firstE;
    }

//    private String conCondition;
    private String conC;

    public String getConCondition() {
        return conC;
    }

    private String secondV;

    public String getSecondValue() {
        return secondV;
    }

    private String secondE;

    public String getSecondExpression() {
        return secondE;
    }

    public SpElSimpleModel(String conditionKey, String firstValue, String firstExpression) {
        this.cKey = conditionKey;
        this.firstV = firstValue;
        this.firstE = firstExpression;
    }

    public SpElSimpleModel and(String secondValue, String secondExpression) {
        this.conC = "and";
        this.secondV = secondValue;
        this.secondE = secondExpression;
        return this;
    }

    public SpElSimpleModel or(String secondValue, String secondExpression) {
        this.conC = "or";
        this.secondV = secondValue;
        this.secondE = secondExpression;
        return this;
    }

    public String toSpElStr() {
        StrBuilder sb = new StrBuilder();
        sb.append(getConditionKey());
        sb.append(" ");
        sb.append(getFirstExpression());
        sb.append(" ");
        sb.append(getFirstValue());
        if (StrUtil.isNotBlank(this.getConCondition())) {
            sb.append(" ");
            sb.append(getConCondition());
            sb.append(" ");

            sb.append(getConditionKey());
            sb.append(" ");
            sb.append(getSecondExpression());
            sb.append(" ");
            sb.append(getSecondValue());
        }
        return sb.toString();
    }

    public static SpElSimpleModel create(String conditionKey, String firstValue, String expression) {
        return  new SpElSimpleModel(conditionKey,firstValue,expression);
    }
}
