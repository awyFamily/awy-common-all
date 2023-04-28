package com.awy.common.excel.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ReflectUtil;
import com.awy.common.excel.AbstractExcelReadWrite;
import com.awy.common.excel.model.ExcelColumnMappingModel;
import com.awy.common.excel.model.ExcelDataColumnModel;
import com.awy.common.excel.utils.ExcelHelper;
import org.apache.commons.compress.utils.Lists;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author 叶红伟
 * @date 2023-04-23
 */
public class SimpleExcelReadWrite<T> extends AbstractExcelReadWrite<T> {

    @Override
    public List<T> readData(Workbook workbook, List<String> columns) {
        try {
            Sheet sheetAt = workbook.getSheetAt(0);
            checkTable(sheetAt,columns);
            Class<T> clazz = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];

            List<T> result = Lists.newArrayList();
            Map<String,Object> rowMap;
            int countRow = sheetAt.getPhysicalNumberOfRows();
            int columnsSize = columns.size();
            Row row;
            for(int i = 1; i < countRow; i++){
                row = sheetAt.getRow(i);
                rowMap = new HashMap<>();
                for(int j = 0; j < columnsSize; j++){
                    rowMap.put(columns.get(j), ExcelHelper.getCellValue(row.getCell(j)));
                }
                result.add(BeanUtil.fillBeanWithMapIgnoreCase(rowMap, ReflectUtil.newInstance(clazz),true));
            }
            return result;
        }finally {
            close(workbook);
        }
    }

    @Override
    public void writeData(Workbook workbook, List<T> dataList, List<ExcelDataColumnModel> columnModels) {

    }

    @Override
    public List<String> getImportColumns(Workbook workbook, List<ExcelColumnMappingModel> columnMappingModels) {
        return columnMappingModels.stream().map(ExcelColumnMappingModel::getColumnName).collect(Collectors.toList());
    }
}
