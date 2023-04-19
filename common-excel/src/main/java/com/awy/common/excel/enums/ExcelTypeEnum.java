package com.awy.common.excel.enums;

import cn.hutool.core.util.StrUtil;
import com.awy.common.excel.constants.PoiPool;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author yhw
 */
@AllArgsConstructor
@Getter
public enum  ExcelTypeEnum {
    HSSF_WORK_BOOK(PoiPool.HSSF_WORK_BOOK),
    XSSF_WORK_BOOK(PoiPool.XSSF_WORK_BOOK),
    ;

    private String suffix;

    public static ExcelTypeEnum getByFileSuffix(String path) {
        if(StrUtil.isEmpty(path)){
            throw new RuntimeException("file path is empty ...");
        }
        if(path.endsWith(PoiPool.XSSF_WORK_BOOK)){
            return XSSF_WORK_BOOK;
        }
        if(path.endsWith(PoiPool.HSSF_WORK_BOOK)){
            return HSSF_WORK_BOOK;
        }
        throw new RuntimeException("file type error ...");
    }
}
