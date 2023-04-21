package com.awy.common.excel.model;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yhw
 * @date 2023-04-21
 */
@NoArgsConstructor
@Data
public class ExcelValidationTypeModel {
    private Integer operatorType;
    private String formula1;
    private String formula2;
    private String[] explicitListOfValues;
//    private ValidationTypeEnum validationType;

    public ExcelValidationTypeModel(String formula1) {
        this.formula1 = formula1;
    }

    public ExcelValidationTypeModel(String[] explicitListOfValues) {
        this.explicitListOfValues = explicitListOfValues;
    }

    public ExcelValidationTypeModel(Integer operatorType, String formula1) {
        this.operatorType = operatorType;
        this.formula1 = formula1;
    }

    public ExcelValidationTypeModel(Integer operatorType, String formula1, String formula2) {
        this.operatorType = operatorType;
        this.formula1 = formula1;
        this.formula2 = formula2;
    }
}