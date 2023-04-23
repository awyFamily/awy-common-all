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
public class ExcelHeadColumnModel extends ExcelColumnModel {

    private int width;

    private String name;

    public int getStandardWidth(){
        return (int)((this.width + 0.72) * 256);
    }

    public ExcelHeadColumnModel(String name) {
        this.name = name;
    }

    public ExcelHeadColumnModel(String name, int width) {
        this.name = name;
        this.width = width;
    }

    public ExcelHeadColumnModel(String name, int width , IndexedColors fillForegroundColor) {
        super(fillForegroundColor);
        this.name = name;
        this.width = width;
    }

    public ExcelHeadColumnModel(String name, int width , IndexedColors fillForegroundColor, ExcelFontModel fontModel) {
        super(fillForegroundColor, fontModel);
        this.name = name;
        this.width = width;
    }

    public ExcelHeadColumnModel(String name, int width , IndexedColors fillForegroundColor, HorizontalAlignment horizontalAlignment, VerticalAlignment valignAlignment, FillPatternType fillPatternType, ExcelFontModel fontModel) {
        super(fillForegroundColor, horizontalAlignment, valignAlignment, fillPatternType, fontModel);
        this.name = name;
        this.width = width;
    }
}
