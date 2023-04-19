package com.awy.common.excel;

import com.awy.common.excel.enums.ExcelTypeEnum;
import com.awy.common.excel.utils.ExcelHelper;

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


}
