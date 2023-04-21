package com.awy.common.excel.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.poi.ss.usermodel.DataValidation;

/**
 * @author yhw
 * @date 2023-04-21
 */
@NoArgsConstructor
@Data
public class ExcelValidationModel {

    private Range range;
    private Box showBox;

    public ExcelValidationModel range(int firstRow, int firstCol) {
        this.range = new Range(firstRow, firstCol);
        return this;
    }

    public ExcelValidationModel range(int firstRow, int lastRow, int firstCol) {
        this.range = new Range(firstRow, lastRow, firstCol);
        return this;
    }

    public ExcelValidationModel range(int firstRow, int lastRow, int firstCol, int lastCol) {
        this.range = new Range(firstRow, lastRow, firstCol, lastCol);
        return this;
    }

    public ExcelValidationModel showErrorBox(String errorTitle, String errorMessage) {
        this.showBox = new Box(DataValidation.ErrorStyle.STOP, errorTitle, errorMessage);
        return this;
    }

    public ExcelValidationModel showWarnBox(String title, String message) {
        this.showBox = new Box(DataValidation.ErrorStyle.WARNING, title, message);
        return this;
    }

    public ExcelValidationModel showInfoBox(String title, String message) {
        this.showBox = new Box(DataValidation.ErrorStyle.INFO, title, message);
        return this;
    }

    @NoArgsConstructor
    @Data
    public static class Range {
        private int firstRow;
        private int lastRow;
        private int firstCol;
        private int lastCol;

        public Range(int firstRow, int firstCol) {
            this(firstRow, firstRow, firstCol);
        }
        public Range(int firstRow, int lastRow, int firstCol) {
            this(firstRow, lastRow, firstCol, firstCol);
        }
        public Range(int firstRow, int lastRow, int firstCol, int lastCol) {
            this.firstRow = firstRow;
            this.lastRow = lastRow;
            this.firstCol = firstCol;
            this.lastCol = lastCol;
        }
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class Box {
        private int boxStyle;
        private String boxTitle;
        private String boxMessage;
    }
}
