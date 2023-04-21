package com.awy.common.excel.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author yhw
 * @date 2023-04-21
 */
@Getter
@AllArgsConstructor
public enum ValidationTypeEnum {

    ANY(0x00),
    INTEGER(0x01),
    DECIMAL(0x02),
    LIST(0x03),
    DATE(0x04),
    TIME(0x05),
    TEXT_LENGTH(0x06),
    FORMULA(0x07)
    ;
    private int code;
}
