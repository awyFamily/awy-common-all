package com.awy.common.excel.impl;

import com.awy.common.excel.AbstractExcelReadWrite;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.HashMap;
import java.util.List;

/**
 * @author 叶红伟
 * @date 2023-04-19
 */
public class MapExcelReadWrite extends AbstractExcelReadWrite<HashMap<String, Object>> {

    @Override
    public List<HashMap<String, Object>> readData(Workbook workbook, String[] columns) {
        return null;
    }

    @Override
    public void writeData(Sheet sheet, CellStyle style, List<HashMap<String, Object>> dataList, String[] columns) {

    }

}
