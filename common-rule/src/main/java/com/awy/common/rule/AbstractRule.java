package com.awy.common.rule;

import com.awy.common.rule.enums.RuleChainNodeTypeNum;
import com.awy.common.rule.model.RuleConfigModel;

/**
 * @author yhw
 * @date 2022-08-01
 */
public abstract class AbstractRule<T extends RuleConfigModel> implements IRule<T> {

    private String name;
    private String groupName;
    private int priority;

    private RuleChainNodeTypeNum chainNodeTypeNum;

    public AbstractRule(String name, int priority, String groupName) {
        this.name = name;
        this.groupName = groupName;
        this.priority = priority;
    }

    public AbstractRule(String name, int priority, String groupName,RuleChainNodeTypeNum ruleChainNodeTypeNum) {
        this.name = name;
        this.groupName = groupName;
        this.priority = priority;
        this.chainNodeTypeNum = ruleChainNodeTypeNum;
    }


    @Override
    public String getGroupName() {
        return this.groupName;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public int getPriority() {
        return this.priority;
    }


    @Override
    public RuleChainNodeTypeNum getChainNodeType() {
        if (this.chainNodeTypeNum == null) {
            return RuleChainNodeTypeNum.fail_end;
        }
        return this.chainNodeTypeNum;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public void setChainNodeTypeNum(RuleChainNodeTypeNum chainNodeTypeNum) {
        this.chainNodeTypeNum = chainNodeTypeNum;
    }
}
