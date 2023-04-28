package com.awy.common.excel.model;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yhw
 * @date 2023-04-28
 */
@NoArgsConstructor
@Data
public class ExcelColumnMappingModel {
    private String headerName;
    private String columnName;
}
