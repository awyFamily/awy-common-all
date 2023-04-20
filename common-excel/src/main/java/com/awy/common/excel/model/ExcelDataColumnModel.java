package com.awy.common.excel.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;

/**
 * @author yhw
 * @date 2023-04-19
 */
@NoArgsConstructor
@Data
public class ExcelDataColumnModel extends ExcelColumnModel {

    private String name;

    public ExcelDataColumnModel(String name) {
        this.name = name;
    }

    public ExcelDataColumnModel(String name, IndexedColors fillForegroundColor) {
        super(fillForegroundColor);
        this.name = name;
    }

    public ExcelDataColumnModel(String name, IndexedColors fillForegroundColor, ExcelFontModel fontModel) {
        super(fillForegroundColor, fontModel);
        this.name = name;
    }

    public ExcelDataColumnModel(String name, IndexedColors fillForegroundColor, HorizontalAlignment horizontalAlignment, VerticalAlignment valignAlignment, FillPatternType fillPatternType, ExcelFontModel fontModel) {
        super(fillForegroundColor, horizontalAlignment, valignAlignment, fillPatternType, fontModel);
        this.name = name;
    }

}
