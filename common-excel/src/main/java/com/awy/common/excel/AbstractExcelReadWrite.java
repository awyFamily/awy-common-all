package com.awy.common.excel;

import cn.hutool.core.util.StrUtil;
import com.awy.common.excel.enums.ExcelTypeEnum;
import com.awy.common.excel.utils.ExcelHelper;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @author yhw
 * @date 2023-04-19
 */
public abstract class AbstractExcelReadWrite<T> implements ExcelReadWrite<T> {

    /**
     * 通过文件路径导入数据
     * @param filePath
     * @param columns
     * @return
     */
    public List<T> importFilePath(String filePath, String[] columns) {
        return readData(ExcelHelper.getImportWorkbook(filePath), columns);
    }

    /**
     * 通过输入流导入数据
     * @param inputStream 数据流
     * @param fileName 文件名称
     * @param columns 列名
     * @return 数据列表
     */
    public List<T> importStream(InputStream inputStream, String fileName, String[] columns) {
        return readData(ExcelHelper.getImportWorkbook(inputStream, ExcelTypeEnum.getByFileSuffix(fileName)), columns);
    }

    public void setCellGBKValue(Cell cell, String value) {
        setCellGBKValue(null,cell,value);
    }

    public int getStandardWidth(Integer width){
        return (int)((width + 0.72) * 256);
    }

    public  void setCellGBKValue(CellStyle style, Cell cell, String value) {
        if(StrUtil.isNotBlank(value) && !"null".equals(value)) {
            cell.setCellValue(value);
        }
        if(style != null){
            cell.setCellStyle(style);
        }
    }

    public void checkTable(Sheet sheetAt, String[] columns){
        //get table head
        Row headRow = sheetAt.getRow(0);
        if(headRow.getPhysicalNumberOfCells() != columns.length){
            throw new RuntimeException("The actual number of columns does not match the expected number of columns");
        }
    }

    public void close(Workbook workbook){
        try {
            if (workbook instanceof SXSSFWorkbook) {
                ((SXSSFWorkbook) workbook).dispose();
            }
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
