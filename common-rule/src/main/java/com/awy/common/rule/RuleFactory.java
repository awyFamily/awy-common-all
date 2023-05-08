package com.awy.common.rule;

import com.awy.common.rule.model.RuleModel;

/**
 * @author yhw
 * @date 2022-08-02
 */
public interface RuleFactory {

    IRule create(RuleModel ruleModel);

}
