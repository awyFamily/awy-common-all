package com.awy.common.excel.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.poi.ss.usermodel.FontUnderline;
import org.apache.poi.ss.usermodel.IndexedColors;

/**
 * @author yhw
 * @date 2023-04-19
 */
@NoArgsConstructor
@Data
public class ExcelFontModel {

    private IndexedColors colors = IndexedColors.BLACK;

    private FontUnderline underline = FontUnderline.NONE;

    private Short height;

    public ExcelFontModel(IndexedColors colors) {
        this.colors = colors;
    }

    public ExcelFontModel(IndexedColors colors, FontUnderline underline, Short height) {
        this.colors = colors;
        this.underline = underline;
        this.height = height;
    }
}
