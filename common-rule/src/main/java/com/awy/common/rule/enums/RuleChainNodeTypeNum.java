package com.awy.common.rule.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 规则链节点类型
 * @author yhw
 * @date 2022-08-04
 */
@AllArgsConstructor
public enum RuleChainNodeTypeNum {

    all_success("所有节点条件满足成功后,代表当前链执行成功"),
    one_success("当前节点条件满足成功后,无需执行后续节点流程,优先级高的节点先得到执行"),
    ;

    public String getId() {
        return name();
    }

    @Getter
    private String description;
}
