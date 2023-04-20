package com.awy.common.excel;

import com.awy.common.excel.model.ExcelDataColumnModel;
import com.awy.common.excel.model.ExcelHeadColumnModel;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.List;

/**
 * @author yhw
 * @date 2023-04-19
 */
public interface ExcelReadWrite<T> {

    List<T> readData(Workbook workbook, String[] columns);

    void writeData(Workbook workbook, List<T> dataList, List<ExcelHeadColumnModel> headColumnModels, List<ExcelDataColumnModel> columnModels);

}
