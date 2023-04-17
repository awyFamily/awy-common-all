package com.awy.common.excel.custom;

import com.awy.common.excel.AbstractExcelUtil;
import com.awy.common.excel.MultiSheetImportVO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.ss.util.CellUtil;

import java.util.List;
import java.util.Map;

/**
 * @author 叶红伟
 * @date 2023-04-17
 */
public class SimpleStyleUtil extends AbstractExcelUtil<MultiSheetImportVO>  {

    @Override
    protected List<Map<String, Object>> readData(Workbook workbook, String[] columns) {
        return null;
    }

    @Override
    protected List<MultiSheetImportVO> readData(Workbook workbook, String[] columns, Class<MultiSheetImportVO> clazz) {
        return null;
    }

    @Override
    protected void writeData(Sheet sheet, CellStyle style, List<MultiSheetImportVO> datas, String[] columns) {

    }

    @Override
    protected void writeCustomizeData(Workbook workbook, List<MultiSheetImportVO> datas, String[] titles, Integer[] widths) {
        Sheet sheet = workbook.createSheet();
        Row row;
        CellStyle style = getCellStyle(workbook, IndexedColors.GREEN,HorizontalAlignment.CENTER,VerticalAlignment.CENTER);

        //创建一个 数据验证helper
        DataValidationHelper dataValidationHelper = sheet.getDataValidationHelper();
        //=== 设置下拉框 =========
        //设置可选值范围
        String[] selectRange = new String[]{"是" , "否"};
        DataValidationConstraint explicitListConstraint = dataValidationHelper.createExplicitListConstraint(selectRange);
        //设置选择框绑定的行列
        CellRangeAddressList  rangeAddressList = new CellRangeAddressList(1, 5000, 2 ,2);
        //创建下拉框辅助选择器
        DataValidation dataValidation = dataValidationHelper.createValidation(explicitListConstraint, rangeAddressList);
        //将验证(下拉框辅助选择器)写入 sheet
        sheet.addValidationData(dataValidation);


        //获取，创建
        CreationHelper creationHelper = workbook.getCreationHelper();
        DataFormat dataFormat = creationHelper.createDataFormat();
        //设置格式，通用
        style.setDataFormat(dataFormat.getFormat("General"));


        //创建表头
        createTitleHead(sheet,style,titles);
    }
}
