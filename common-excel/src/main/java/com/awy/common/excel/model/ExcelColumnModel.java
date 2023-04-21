package com.awy.common.excel.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.poi.ss.usermodel.*;

/**
 * @author yhw
 * @date 2023-04-19
 */
@NoArgsConstructor
@Data
public class ExcelColumnModel {


    //    private int height;
    private IndexedColors fillForegroundColor;

    private HorizontalAlignment horizontalAlignment;

    private VerticalAlignment valignAlignment;

    private FillPatternType fillPatternType = FillPatternType.SOLID_FOREGROUND;

    private ExcelFontModel fontModel;


    public ExcelColumnModel(IndexedColors fillForegroundColor) {
        this(fillForegroundColor, null);
    }

    public ExcelColumnModel(IndexedColors fillForegroundColor, ExcelFontModel fontModel) {
        this(fillForegroundColor, HorizontalAlignment.CENTER, VerticalAlignment.CENTER, FillPatternType.SOLID_FOREGROUND, fontModel);
    }

    public ExcelColumnModel(IndexedColors fillForegroundColor, HorizontalAlignment horizontalAlignment, VerticalAlignment valignAlignment, FillPatternType fillPatternType, ExcelFontModel fontModel) {
        this.fillForegroundColor = fillForegroundColor;
        this.horizontalAlignment = horizontalAlignment;
        this.valignAlignment = valignAlignment;
        this.fillPatternType = fillPatternType;
        this.fontModel = fontModel;
    }

    public CellStyle toCellStyle(Workbook workbook) {
        CellStyle cellStyle = workbook.createCellStyle();
        if (this.fillForegroundColor != null) {
            cellStyle.setFillForegroundColor(this.fillForegroundColor.getIndex());
        }
        if (this.fillPatternType != null) {
            cellStyle.setFillPattern(this.fillPatternType);
        }
        if (this.horizontalAlignment != null) {
            cellStyle.setAlignment(this.horizontalAlignment);
        }
        if (this.valignAlignment != null) {
            cellStyle.setVerticalAlignment(this.valignAlignment);
        }
        if (this.fontModel != null) {
            Font font = workbook.createFont();
            font.setUnderline(this.fontModel.getUnderline().getByteValue());
            font.setColor(this.fontModel.getColors().getIndex());
            font.setFontHeightInPoints(this.fontModel.getHeight());
            cellStyle.setFont(font);
        }
        cellStyle.setWrapText(true);
        // cellStyle.setBorderBottom(BorderStyle.THIN);
        // cellStyle.setBorderLeft(BorderStyle.THIN);
        // cellStyle.setBorderRight(BorderStyle.THIN);
        // cellStyle.setBorderTop(BorderStyle.THIN);
        return cellStyle;

    }
}
