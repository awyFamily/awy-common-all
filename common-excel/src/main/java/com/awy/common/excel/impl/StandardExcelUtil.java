package com.awy.common.excel.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ReflectUtil;
import com.awy.common.excel.AbstractExcelUtil;
import org.apache.commons.compress.utils.Lists;
import org.apache.poi.ss.usermodel.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 标准excel工具类
 * @param <T>
 * @author yhw
 */
public class StandardExcelUtil<T> extends AbstractExcelUtil<T> {

    @Override
    protected List<Map<String,Object>> readData(Workbook workbook, String[] columns) {
        try {
            Sheet sheetAt = workbook.getSheetAt(0);

            checkTable(sheetAt,columns);

            int columnsSize = columns.length;

            List<Map<String,Object>> result = Lists.newArrayList();
            Map<String,Object> rowMap;

            int countRow = sheetAt.getPhysicalNumberOfRows();
            Row row;
            for(int i = 1; i < countRow; i++){
                row = sheetAt.getRow(i);
                rowMap = new HashMap<>();
                for(int j = 0; j < columnsSize; j++){
                    rowMap.put(columns[j], getCellValue(row.getCell(j)));
                }
                result.add(rowMap);
            }


            return result;
        } finally {
            close(workbook);
        }
    }

    @Override
    protected List<T> readData(Workbook workbook, String[] columns, Class<T> clazz) {
        try {
            Sheet sheetAt = workbook.getSheetAt(0);

            checkTable(sheetAt,columns);

            int columnsSize = columns.length;

            List<T> result = Lists.newArrayList();
            T t;
            Map<String,Object> rowMap;

            int countRow = sheetAt.getPhysicalNumberOfRows();
            Row row;
            for(int i = 1; i < countRow; i++){
                row = sheetAt.getRow(i);
                rowMap = new HashMap<>();
                for(int j = 0; j < columnsSize; j++){
                    rowMap.put(columns[j], getCellValue(row.getCell(j)));
                }
                result.add(BeanUtil.fillBeanWithMapIgnoreCase(rowMap,ReflectUtil.newInstance(clazz),true));
            }

            return result;
        }finally {
            close(workbook);
        }
    }

    @Override
    protected void writeData(Sheet sheet, CellStyle style, List<T> datas, String[] columns) {
        T t;
        Row row;
        for (int i = 0; i < datas.size() ; i++) {
            t = datas.get(i);
            row = sheet.createRow((i + 1));
            for(int j = 0; j < columns.length; j++){
                try {
                    setCellGBKValue(style,row.createCell(j, CellType.STRING),getValue(t, columns[j]));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void checkTable(Sheet sheetAt,String[] columns){
        //get table head
        Row headRow = sheetAt.getRow(0);
        if(headRow.getPhysicalNumberOfCells() != columns.length){
            throw new RuntimeException("The actual number of columns does not match the expected number of columns");
        }
    }


    @Override
    protected void writeCustomizeData(Workbook workbook,List<T> datas,String[] titles,Integer[] widths) {
        //标准工具类，不提供自定义实现
    }


}
