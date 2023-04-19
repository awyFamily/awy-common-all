package com.awy.common.excel.constants;

public interface PoiPool {

    String HSSF_WORK_BOOK = ".xls";

    String XSSF_WORK_BOOK = ".xlsx";

    Boolean IS_NATIVE_FILE = Boolean.TRUE;

    Boolean NOT_NATIVE_FILE = Boolean.FALSE;

    String EXPORT_EXCEL_PATH = "excelExport";

    String import_EXCEL_PATH = "excelImport";

    int DEFAULT_PAGE_SIZE = 65535;

    int DEFAULT_SINGLE_FILE_ROWS = DEFAULT_PAGE_SIZE << 1;

    String HTTP = "http";
    String HTTPS = "https";
}
