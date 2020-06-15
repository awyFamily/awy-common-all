package com.yhw.nc.common.excel.enums;

import com.yhw.nc.common.excel.constants.PoiPool;
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

//    private
}
