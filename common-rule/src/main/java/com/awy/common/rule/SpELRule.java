package com.awy.common.rule;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.awy.common.rule.enums.RuleChainNodeTypeNum;
import com.awy.common.rule.enums.RuleTypeEnum;
import com.awy.common.rule.model.SpElModel;
import com.awy.common.rule.model.SpElSimpleModel;
import lombok.Setter;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.util.List;
import java.util.Map;

/**
 * @author yhw
 * @date 2022-08-15
 */
public abstract class SpELRule extends AbstractRule<SpElModel> {

    @Setter
    private Boolean andConCondition;

    private ExpressionParser parser;

    public SpELRule(String name, int priority, String groupName) {
        this(name, priority, groupName,RuleChainNodeTypeNum.fail_end);
    }

    public SpELRule(String name, int priority, String groupName, RuleChainNodeTypeNum ruleChainNodeTypeNum) {
        super(name, priority, groupName, ruleChainNodeTypeNum);
        parser = new SpelExpressionParser();
    }

    public abstract Map<String, SpElSimpleModel> getConditionMap();

    public abstract Boolean addCondition(SpElSimpleModel model);

    @Override
    public RuleTypeEnum getType() {
        return RuleTypeEnum.SP_EL;
    }

    @Override
    public boolean isSupport(String key, String condition) {
        if (!JSONUtil.isTypeJSON(condition)) {
            return false;
        }
        JSONObject json = JSONUtil.parseObj(condition);
        Map<String, SpElSimpleModel> conditionMap = getConditionMap();
        Boolean hasSuccess;
        for (String conditionKey : json.keySet()) {
            SpElSimpleModel model = conditionMap.get(conditionKey);
            if (model != null) {
                model.setCKey(json.getStr(conditionKey));
                hasSuccess = parser.parseExpression(model.toSpElStr()).getValue(Boolean.class);
                //add condition
                if (this.andConCondition) {
                    if (!hasSuccess) {
                        return false;
                    }
                } else {
                    if (hasSuccess) {
                        return true;
                    }
                }
            }
        }
        return true;
    }

    @Override
    public boolean successCallback(String key, String condition) {
        return true;
    }

    @Override
    public boolean handler(String key, String condition) {
        if(isSupport(key, condition)) {
            return successCallback(key, condition);
        }
        return false;
    }

    @Override
    public void buildRuleConfig(SpElModel model) {
        if (model == null) {
            return;
        }
        setAndConCondition(model.getHasAndConCondition());
        if (StrUtil.isNotBlank(model.getEls())) {
            List<SpElSimpleModel> array = JSONUtil.parseArray(model.getEls()).toList(SpElSimpleModel.class);
            for (SpElSimpleModel simpleModel : array) {
                addCondition(simpleModel);
            }
        }
    }
}
