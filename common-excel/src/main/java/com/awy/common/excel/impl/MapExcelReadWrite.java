package com.awy.common.excel.impl;

import com.awy.common.excel.AbstractExcelReadWrite;
import com.awy.common.excel.model.ExcelColumnMappingModel;
import com.awy.common.excel.model.ExcelDataColumnModel;
import com.awy.common.excel.utils.ExcelHelper;
import org.apache.commons.compress.utils.Lists;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author yhw
 * @date 2023-04-19
 */
public class MapExcelReadWrite extends AbstractExcelReadWrite<Map<String, Object>> {

    @Override
    public List<Map<String, Object>> readData(Workbook workbook, List<String> columns) {
        Sheet sheetAt = workbook.getSheetAt(0);
        checkTable(sheetAt,columns);
        int columnsSize = columns.size();

        List<Map<String,Object>> result = Lists.newArrayList();
        Map<String,Object> rowMap;
        int countRow = sheetAt.getPhysicalNumberOfRows();
        Row row;
        for(int i = 1; i < countRow; i++){
            row = sheetAt.getRow(i);
            rowMap = new HashMap<>();
            for(int j = 0; j < columnsSize; j++){
                rowMap.put(columns.get(j), ExcelHelper.getCellValue(row.getCell(j)));
            }
            result.add(rowMap);
        }
        return result;
    }

    @Override
    public void writeData(Workbook workbook, List<Map<String, Object>> dataList, List<ExcelDataColumnModel> columnModels) {
        Sheet sheet = workbook.getSheetAt(0);
        Map<String, Object> map;
        Row row;
        for(int i = 0; i < dataList.size(); i++){
            map = dataList.get(i);
            row = sheet.createRow(i + 1);
            for(int j = 0; j < columnModels.size(); j++){
                try {
                    ExcelDataColumnModel columnModel = columnModels.get(j);
                    setCellGBKValue(columnModel.toCellStyle(workbook), row.createCell(j, CellType.STRING), ExcelHelper.formatValue(map.get(columnModel.getName())).toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    @Override
    public List<String> getImportColumns(Workbook workbook, List<ExcelColumnMappingModel> columnMappingModels) {
        return columnMappingModels.stream().map(ExcelColumnMappingModel::getColumnName).collect(Collectors.toList());
    }
}
