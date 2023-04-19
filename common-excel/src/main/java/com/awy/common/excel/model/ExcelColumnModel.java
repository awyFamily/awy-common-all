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
public class ExcelColumnModel {

    private int width;

    private int height;

    private IndexedColors fillForegroundColor;

    private HorizontalAlignment horizontalAlignment;

    private VerticalAlignment valignAlignment;

    private FillPatternType fillPatternType = FillPatternType.SOLID_FOREGROUND;

    private ExcelFontModel fontModel;

    public ExcelColumnModel(int width, int height) {
        this(width, height, null, null);
    }

    public ExcelColumnModel(int width, int height, IndexedColors fillForegroundColor) {
        this(width, height, fillForegroundColor, null);
    }

    public ExcelColumnModel(int width, int height, IndexedColors fillForegroundColor, ExcelFontModel fontModel) {
        this(width, height, fillForegroundColor, HorizontalAlignment.CENTER, VerticalAlignment.CENTER, FillPatternType.SOLID_FOREGROUND, fontModel);
    }

    public ExcelColumnModel(int width, int height, IndexedColors fillForegroundColor, HorizontalAlignment horizontalAlignment, VerticalAlignment valignAlignment, FillPatternType fillPatternType, ExcelFontModel fontModel) {
        this.width = width;
        this.height = height;
        this.fillForegroundColor = fillForegroundColor;
        this.horizontalAlignment = horizontalAlignment;
        this.valignAlignment = valignAlignment;
        this.fillPatternType = fillPatternType;
        this.fontModel = fontModel;
    }
}
