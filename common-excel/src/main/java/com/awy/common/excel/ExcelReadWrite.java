package com.awy.common.excel;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.List;

/**
 * @author yhw
 * @date 2023-04-19
 */
public interface ExcelReadWrite<T> {

    List<T> readData(Workbook workbook, String[] columns);

    void writeData(Sheet sheet, CellStyle style, List<T> dataList, String[] columns);

}
