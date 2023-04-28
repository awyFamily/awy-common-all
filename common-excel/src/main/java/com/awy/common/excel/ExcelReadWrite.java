package com.awy.common.excel;

import com.awy.common.excel.model.ExcelDataColumnModel;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.List;

/**
 * @author yhw
 * @date 2023-04-19
 */
public interface ExcelReadWrite<T> {

    List<T> readData(Workbook workbook, List<String> columns);

    void writeData(Workbook workbook, List<T> dataList, List<ExcelDataColumnModel> columnModels);

}
