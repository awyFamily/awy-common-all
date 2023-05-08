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

    fail_end("当前节点失败,直接结束，即整个链流程都失败"),
    success_end("当前节点条件满足成功后,无需执行后续节点流程,优先级高的节点先得到执行"),
    fail_continue("当前节点不满足条件,跳过此节点，继续执行后续节点"),
    success_end_fail_continue("当前节点条件满足成功无需执行后续节点,当前节点失败,忽略此次失败"),
    ;


    public String getId() {
        return name();
    }

    @Getter
    private String description;
}
