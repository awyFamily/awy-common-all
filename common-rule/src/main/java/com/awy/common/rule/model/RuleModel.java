package com.awy.common.rule.model;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import lombok.Data;

/**
 * @author yhw
 * @date 2022-08-03
 */
@Data
public class RuleModel {

    private String name;

    private String groupName = "default";

    private int priority;

    private String ruleType;

    private String ruleChainNodeType;

    private String expand;

    public <T> T getExpandBean(Class<T> clazz) {
        if (StrUtil.isBlank(this.expand)) {
            return null;
        }
        return JSONUtil.toBean(this.expand,clazz);
    }
}
