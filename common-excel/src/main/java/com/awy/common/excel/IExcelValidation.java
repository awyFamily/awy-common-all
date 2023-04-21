package com.awy.common.excel;

import com.awy.common.excel.enums.ExcelOperatorTypeEnum;
import com.awy.common.excel.enums.ValidationTypeEnum;
import com.awy.common.excel.model.ExcelValidationModel;
import com.awy.common.excel.model.ExcelValidationTypeModel;
import com.awy.common.util.utils.DateJdK8Util;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

/**
 * @author yhw
 * @date 2023-04-21
 */
public interface IExcelValidation {

    ValidationTypeEnum getValidationType();

    ExcelValidationModel getValidationModel();

    ExcelValidationTypeModel getValidationTypeModel();

    class AnyValidationModel implements IExcelValidation {
        @Override
        public ValidationTypeEnum getValidationType() {
            return ValidationTypeEnum.ANY;
        }
        @Override
        public ExcelValidationModel getValidationModel() {
            return null;
        }
        @Override
        public ExcelValidationTypeModel getValidationTypeModel() {
            return null;
        }
    }

    abstract class BaseValidation implements IExcelValidation{
        @Setter
        private ExcelValidationModel validationModel;

        @Override
        public ExcelValidationModel getValidationModel() {
            return this.validationModel;
        }
    }

    class ListValidation extends BaseValidation implements IExcelValidation {
        private String[] listOfValues;

        public ListValidation(String[] listOfValues, ExcelValidationModel validationModel) {
            this.listOfValues = listOfValues;
            setValidationModel(validationModel);
        }

        @Override
        public ValidationTypeEnum getValidationType() {
            return ValidationTypeEnum.LIST;
        }

        @Override
        public ExcelValidationTypeModel getValidationTypeModel() {
            return new ExcelValidationTypeModel(listOfValues);
        }
    }

    class CustomValidation extends BaseValidation  implements IExcelValidation {
        private String formula;

        public CustomValidation(String formula, ExcelValidationModel validationModel) {
            this.formula = formula;
            setValidationModel(validationModel);
        }

        @Override
        public ValidationTypeEnum getValidationType() {
            return ValidationTypeEnum.FORMULA;
        }

        @Override
        public ExcelValidationTypeModel getValidationTypeModel() {
            return new ExcelValidationTypeModel(formula);
        }
    }

    class IntValidation extends BaseValidation implements IExcelValidation {
        private Integer minValue;
        private Integer maxValue;
        private  ExcelOperatorTypeEnum operatorType;

        public IntValidation(Integer minValue, Integer maxValue, ExcelOperatorTypeEnum operatorType, ExcelValidationModel validationModel) {
            this.minValue = minValue;
            this.maxValue = maxValue;
            this.operatorType = operatorType;
            setValidationModel(validationModel);
        }

        @Override
        public ValidationTypeEnum getValidationType() {
            return ValidationTypeEnum.INTEGER;
        }

        @Override
        public ExcelValidationTypeModel getValidationTypeModel() {
            return new ExcelValidationTypeModel(operatorType.getCode(), minValue.toString(), maxValue.toString());
        }
    }

    class DecimalValidation extends BaseValidation implements IExcelValidation {
        private BigDecimal minValue;
        private BigDecimal maxValue;
        private ExcelOperatorTypeEnum operatorType;

        public DecimalValidation(BigDecimal minValue, BigDecimal maxValue, ExcelOperatorTypeEnum operatorType, ExcelValidationModel validationModel) {
            this.minValue = minValue;
            this.maxValue = maxValue;
            this.operatorType = operatorType;
            setValidationModel(validationModel);
        }

        @Override
        public ValidationTypeEnum getValidationType() {
            return ValidationTypeEnum.DECIMAL;
        }

        @Override
        public ExcelValidationTypeModel getValidationTypeModel() {
            String minValueStr = minValue != null ? minValue.setScale(2, RoundingMode.HALF_UP).toString() : "";
            String maxValueStr = maxValue != null ? maxValue.setScale(2, RoundingMode.HALF_UP).toString() : "";
            return new ExcelValidationTypeModel(operatorType.getCode(), minValueStr, maxValueStr);
        }
    }

    class DateValidation extends BaseValidation implements IExcelValidation {
        private LocalDateTime minTime;
        private LocalDateTime maxTime;
        private ExcelOperatorTypeEnum operatorType;

        public DateValidation(LocalDateTime minTime, LocalDateTime maxTime, ExcelOperatorTypeEnum operatorType, ExcelValidationModel validationModel) {
            this.minTime = minTime;
            this.maxTime = maxTime;
            this.operatorType = operatorType;
            setValidationModel(validationModel);
        }

        @Override
        public ValidationTypeEnum getValidationType() {
            return ValidationTypeEnum.DATE;
        }

        @Override
        public ExcelValidationTypeModel getValidationTypeModel() {
            return new ExcelValidationTypeModel(operatorType.getCode(), minTime != null ? DateJdK8Util.formatLocalDateTime(minTime) : "", maxTime != null ? DateJdK8Util.formatLocalDateTime(maxTime) : "");
        }

    }


}
