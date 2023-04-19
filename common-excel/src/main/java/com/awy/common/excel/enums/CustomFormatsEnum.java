package com.awy.common.excel.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.poi.ss.usermodel.DataFormat;

import java.util.HashMap;
import java.util.Map;

/**
 * 自定义格式
 * @author yhw
 * @date 2023-04-18
 */
@Getter
@AllArgsConstructor
public enum CustomFormatsEnum {

    date_yyyy_m_d_h_mm("yyyy/m/d h:mm", "时间格式"),
    date_yyyy_m_d_h_mm_ss("yyyy/m/d h:mm:ss", "日期格式"),
    date_yyyy_mm_dd_h_mm_ss("yyyy/mm/dd h:mm:ss", "日期格式"),
    date_yyyy_mm_dd_hh_mm_ss("yyyy/mm/dd hh:mm:ss", "日期格式"),
    ;

    private String format;

    private String description;


    /**
     * 为了统一操作，避免强转使用 getFormat 对自定义格式进行初始化;
     * 当前方法通常在,导出excel,需要自定义格式时，可以O用到
     * @param dataFormat
     * @return 自定义格式化类型
     */
    public static Map<String, Short> initCustomFormat(DataFormat dataFormat) {
        if (dataFormat == null) {
            return new HashMap<>();
        }
        HashMap<String, Short> customFormatMap = new HashMap<>();
        for (CustomFormatsEnum formatsEnum : CustomFormatsEnum.values()) {
            customFormatMap.put(formatsEnum.getFormat(), dataFormat.getFormat(formatsEnum.getFormat()));
        }
        return customFormatMap;
//        //也可以不用对格式进行初始化(自己新建的表格,不通过poi生成，需要执行读取操作),但是
//        // xssf 需要调用 StylesTable, 进行Get操作
//        // hssf 需要调用 InternalWorkbook， 进行 getFormat 然后执行 createFormat, 返回 index
//        if (dataFormat instanceof XSSFDataFormat) {
//            //XSSFDataFormat.getFormat if can't find it then add a new  format
//            XSSFDataFormat xssfDataFormat = (XSSFDataFormat) dataFormat;
//            //可以自己put，也可以调用 getFormat 返回 index
//            //xssfDataFormat.putFormat(date_yyyy_m_d_h_mm.getShortNumber(), "");
//        } else {
//            //HSSFDataFormat.getFormat if can't find it then add a new  format
//            HSSFDataFormat hssfDataFormat = (HSSFDataFormat) dataFormat;
//            //hssfDataFormat.getFormat()
//        }
    }
}
