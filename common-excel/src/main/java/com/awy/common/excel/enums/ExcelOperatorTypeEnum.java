package com.awy.common.excel.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author yhw
 * @date 2023-04-21
 */
@Getter
@AllArgsConstructor
public enum ExcelOperatorTypeEnum {

    BETWEEN(0x00),
    NOT_BETWEEN(0x01),
    EQUAL(0x02),
    NOT_EQUAL(0x03),
    GREATER_THAN(0x04),
    LESS_THAN(0x05),
    GREATER_OR_EQUAL(0x06),
    LESS_OR_EQUAL(0x07),
    IGNORED(0x00),
    ;

    private int code;
}
