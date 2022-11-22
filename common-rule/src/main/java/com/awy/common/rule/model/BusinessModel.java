package com.awy.common.rule.model;

import lombok.Data;

import java.util.Map;

/**
 * @author yhw
 * @date 2022-08-19
 */
@Data
public class BusinessModel extends RuleConfigModel {

    private Map<String,Object> config;
}
