package com.awy.common.excel.custom;

import com.awy.common.excel.AbstractExcelUtil;
import com.awy.common.excel.MultiSheetImportVO;
import com.awy.common.excel.enums.BuiltinFormatsEnum;
import com.awy.common.excel.enums.CustomFormatsEnum;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;

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
        // 阻止输入非下拉选项的值
        dataValidation.setErrorStyle(DataValidation.ErrorStyle.STOP);
        dataValidation.setShowErrorBox(true);
        dataValidation.setSuppressDropDownArrow(true);
        dataValidation.createErrorBox("提示", "请选择下拉框内的数据");
        //将验证(下拉框辅助选择器)写入 sheet
        sheet.addValidationData(dataValidation);

        //设置验证生效的范围(excel起始行，结束行，起始列，结束列)
        rangeAddressList = new CellRangeAddressList(1, 5000, 4, 4);
        //formula1 参数，对应的是 excel 函数 ： https://support.microsoft.com/zh-cn/office/%E5%85%AC%E5%BC%8F%E5%92%8C%E5%87%BD%E6%95%B0-294d9486-b332-48ed-b489-abe7d0f9eda9#ID0EAABAAA=Functions
        DataValidationConstraint dateConstraint = dataValidationHelper.createDateConstraint(DataValidationConstraint.OperatorType.BETWEEN, "Date(2000, 1, 1)", "Date(2100, 12, 31)", CustomFormatsEnum.date_yyyy_mm_dd_hh_mm_ss.getFormat());
        dataValidation = dataValidationHelper.createValidation(dateConstraint, rangeAddressList);
        dataValidation.setErrorStyle(DataValidation.ErrorStyle.STOP);
        dataValidation.createErrorBox("提示", "请输入指定格式日期,范围:[2000-01-01 00:00:00,2100-12-31 00:00:00]");
        dataValidation.setShowErrorBox(true);
        sheet.addValidationData(dataValidation);

        //设置，文本框必填校验 ： https://support.microsoft.com/zh-cn/office/%E4%BD%BF%E7%94%A8%E6%9C%89%E6%95%88%E6%80%A7%E8%A7%84%E5%88%99%E9%99%90%E5%88%B6%E6%95%B0%E6%8D%AE%E8%BE%93%E5%85%A5-b91c6b15-bcd3-42c1-90bf-e3a0272e988d
        dateConstraint = dataValidationHelper.createTextLengthConstraint(DataValidationConstraint.OperatorType.BETWEEN, "5","20");
        rangeAddressList = new CellRangeAddressList(1, 5000, 6, 6);
        dataValidation = dataValidationHelper.createValidation(dateConstraint, rangeAddressList);
        dataValidation.setErrorStyle(DataValidation.ErrorStyle.STOP);
        dataValidation.createErrorBox("提示", "请输入长度5-30长度的字符");
        dataValidation.setShowErrorBox(true);
        sheet.addValidationData(dataValidation);

        //必填，自定义
//        dataValidationHelper.create

        //获取 creationHelper
        CreationHelper creationHelper = workbook.getCreationHelper();
//        dataFormat.

        //设置格式，通用例如:【=LEN(A1)=10】  约定A1列长度必须等于10，当指定了 range 则，忽略A1，对范围列都生效
        //例如：【=AND(LEN(B1)<10,LEN(B1)>1)】，约定B列, 长度必须大于1，小于10
        style.setDataFormat(BuiltinFormatsEnum.General.getShortNumber());


        //创建表头
//        createTitleHead(sheet,style,titles);
        Row row = createXSSFRow(sheet, 0);
        Cell cell = row.createCell(0, CellType.STRING);
        cell.setCellStyle(style);
        cell.setCellValue("General 类型");

        //第二列
        cell = row.createCell(1, CellType.STRING);
        style.setDataFormat(BuiltinFormatsEnum.integer.getShortNumber());
        cell.setCellStyle(style);
        cell.setCellValue("整数类型");

        //第三列
        cell = row.createCell(2, CellType.STRING);
        style.setDataFormat(BuiltinFormatsEnum.General.getShortNumber());
        cell.setCellStyle(style);
        cell.setCellValue("下拉选择器");

        cell = row.createCell(3, CellType.STRING);
        style.setDataFormat(BuiltinFormatsEnum.date_m_d_yy_h_mm.getShortNumber());
        cell.setCellStyle(style);
        cell.setCellValue("时间类型");

        //自定义数据样式
        Map<String, Short> customFormatMap = CustomFormatsEnum.initCustomFormat(creationHelper.createDataFormat());
        cell = row.createCell(4, CellType.STRING);
        style.setDataFormat(customFormatMap.get(CustomFormatsEnum.date_yyyy_mm_dd_hh_mm_ss.getFormat()));
        cell.setCellStyle(style);
        cell.setCellValue("自定义时间类型");

        cell = row.createCell(5, CellType.BOOLEAN);
        style.setDataFormat(BuiltinFormatsEnum.General.getShortNumber());
        cell.setCellStyle(style);
        cell.setCellValue("布尔值");

        cell = row.createCell(6, CellType.STRING);
        style.setDataFormat(BuiltinFormatsEnum.General.getShortNumber());
        cell.setCellStyle(style);
        cell.setCellValue("必填值*");


        //https://blog.51cto.com/u_15127538/4525797
        //http://www.python66.com/itmianshiti/670.html 自定义校验函数示例
        //https://support.microsoft.com/zh-cn/office/cell-%E5%87%BD%E6%95%B0-51bd39a5-f338-4dbe-a33f-955d67c2b2cf
    }
}
