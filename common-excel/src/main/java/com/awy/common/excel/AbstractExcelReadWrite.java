package com.awy.common.excel;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.util.StrUtil;
import com.awy.common.excel.constants.PoiPool;
import com.awy.common.excel.enums.ExcelTypeEnum;
import com.awy.common.excel.model.ExcelColumnMappingModel;
import com.awy.common.excel.model.ExcelDataColumnModel;
import com.awy.common.excel.model.ExcelHeadColumnModel;
import com.awy.common.excel.utils.ExcelHelper;
import com.awy.common.util.utils.CollUtil;
import com.awy.common.util.utils.DateJdK8Util;
import com.awy.common.util.utils.FileUtil;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yhw
 * @date 2023-04-19
 */
public abstract class AbstractExcelReadWrite<T> implements ExcelReadWrite<T> {

    /**
     * 获取导入列的属性列表
     * @param workbook 工作薄
     * @param columnMappingModels 列映射
     * @return 列名映射列表
     */
    public abstract List<String> getImportColumns(Workbook workbook, List<ExcelColumnMappingModel> columnMappingModels);

    /**
     * 通过文件路径导入数据
     * @param filePath
     * @param columnMappingModels
     * @return
     */
    public List<T> importFilePath(String filePath, List<ExcelColumnMappingModel> columnMappingModels) {
        return doReadData(ExcelHelper.getImportWorkbook(filePath), columnMappingModels);
    }

    /**
     * 通过输入流导入数据
     * @param inputStream 数据流
     * @param fileName 文件名称
     * @param columnMappingModels 列名映射
     * @return 数据列表
     */
    public List<T> importStream(InputStream inputStream, String fileName, List<ExcelColumnMappingModel> columnMappingModels) {
        return doReadData(ExcelHelper.getImportWorkbook(inputStream, ExcelTypeEnum.getByFileSuffix(fileName)), columnMappingModels);
    }

    private List<T> doReadData(Workbook workbook, List<ExcelColumnMappingModel> columnMappingModels) {
        List<String> columns = getImportColumns(workbook, columnMappingModels);
        List<T> result = readData(workbook, columns);
        close(workbook);
        return result;
    }

    public String exportTemplate(String excelName, String folderPath, String sheetName, List<ExcelHeadColumnModel> headColumnModels, List<IExcelValidation> excelValidations) {
        Workbook workbook = this.processExport(sheetName, headColumnModels, excelValidations, new ArrayList<>(), new ArrayList<>());
        return this.getExportPath(excelName, folderPath, workbook);
    }

    private String getExportPath(String excelName,String folderPath,Workbook  workbook){
        String excelPath;
        String fileName = excelName.concat("_").concat(DateJdK8Util.formatLocalDateTime(LocalDateTime.now(), DatePattern.PURE_DATETIME_PATTERN));
        if (StrUtil.isBlank(folderPath)) {
            excelPath = FileUtil.getCurrentDataFormatFilePath(PoiPool.EXPORT_EXCEL_PATH, fileName);
        } else {
            excelPath = folderPath.concat(File.separator).concat(fileName).concat(PoiPool.XSSF_WORK_BOOK);
        }
        try(OutputStream outputStream = new FileOutputStream(excelPath)){
            workbook.write(outputStream);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            close(workbook);
        }
        return excelPath;
    }

    public byte[] exportTemplate(String sheetName, List<ExcelHeadColumnModel> headColumnModels, List<IExcelValidation> excelValidations) {
        Workbook workbook = this.processExport(sheetName, headColumnModels, excelValidations, new ArrayList<>(), new ArrayList<>());
        return this.convertWorkbookToBytes(workbook);
    }

    private Workbook processExport(String sheetName, List<ExcelHeadColumnModel> headColumnModels, List<IExcelValidation> excelValidations, List<ExcelDataColumnModel> columnModels, List<T> dataList) {
        Workbook workbook = ExcelHelper.createSXssfWorkbook();
        Sheet sheet = workbook.createSheet(sheetName);
        // create header row
        this.initHeaderRow(workbook, sheet, headColumnModels);

        // apply validations
        if (CollUtil.isNotEmpty(excelValidations)) {
            for (IExcelValidation validation : excelValidations) {
                sheet.addValidationData(ExcelHelper.createDataValidation(validation, sheet));
            }
        }
        if (CollUtil.isNotEmpty(dataList)) {
            this.writeData(workbook, dataList, columnModels);
        }
        return workbook;
    }

    public void initHeaderRow(Workbook workbook, Sheet sheet, List<ExcelHeadColumnModel> headColumnModels) {
        ExcelHeadColumnModel headColumnModel;
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headColumnModels.size(); i++) {
            headColumnModel = headColumnModels.get(i);
            setCellGBKValue(headColumnModel.toCellStyle(workbook), headerRow.createCell(i), headColumnModel.getName());
            sheet.setColumnWidth(i, headColumnModel.getStandardWidth());
        }
    }

    public byte[] convertWorkbookToBytes(Workbook workbook) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            close(workbook);
        }
    }

    public void setCellGBKValue(Cell cell, String value) {
        setCellGBKValue(null,cell,value);
    }

    public  void setCellGBKValue(CellStyle style, Cell cell, String value) {
        if(StrUtil.isNotBlank(value) && !"null".equals(value)) {
            cell.setCellValue(value);
        }
        if(style != null){
            cell.setCellStyle(style);
        }
    }

    public void checkTable(Sheet sheetAt, List<String> columns){
        //get table head
        Row headRow = sheetAt.getRow(0);
        if(headRow.getPhysicalNumberOfCells() != columns.size()){
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
