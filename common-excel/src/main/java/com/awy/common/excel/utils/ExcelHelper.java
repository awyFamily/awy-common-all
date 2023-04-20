package com.awy.common.excel.utils;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.util.StrUtil;
import com.awy.common.excel.constants.PoiPool;
import com.awy.common.excel.enums.ExcelTypeEnum;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @author 叶红伟
 * @date 2023-04-19
 */
public class ExcelHelper {

    public static Workbook getImportWorkbook(String path){
        if (StrUtil.isBlank(path)) {
            return null;
        }
        boolean isNativeFile = isNativeFile(path);
        ExcelTypeEnum typeEnum = ExcelTypeEnum.getByFileSuffix(path);
        try(InputStream inputStream = isNativeFile ? new FileInputStream(path) : uploadFile(path)){
            return getImportWorkbook(inputStream, typeEnum);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public static Workbook getImportWorkbook(InputStream inputStream,ExcelTypeEnum typeEnum){
        if(inputStream == null){
            throw new RuntimeException("input stream is null");
        }
        Workbook workbook;
        try(inputStream){
            switch (typeEnum){
                case XSSF_WORK_BOOK:
                    workbook = createXssfWorkbook(inputStream);
                    break;
                case HSSF_WORK_BOOK:
                    workbook = createHssfWorkbook(inputStream);
                    break;
                default:
                    throw new RuntimeException("excel type not exists");
            }
        }catch (Exception e){
            throw new RuntimeException(e);
        }
        return workbook;
    }

    /**
     * 读取Excel
     * @param inputStream 流
     * @return workBook
     */
    public static Workbook createXssfWorkbook(InputStream inputStream){
        try {
            return new XSSFWorkbook(inputStream);
            ////SXSSFSheet -> _xFromSxHash(读取的文件不可以读取行)  , _sxFromXHash(读取的文件可以读取行)
            //return new SXSSFWorkbook(new XSSFWorkbook(inputStream));
        } catch (IOException e) {
            throw new RuntimeException("file type illegal",e);
        }
    }

    public static Workbook createHssfWorkbook(InputStream inputStream){
        try {
            return new HSSFWorkbook(inputStream);
        } catch (IOException e) {
            throw new RuntimeException("file type illegal",e);
        }
    }

    /**
     * 创建SXSSFWorkbook，用于大批量数据写出
     * 创建以【.xlsx】为后缀的表格(只写, Stream 的方式)
     * @return xlsx table
     */
    public static Workbook createSXssfWorkbook(){
//        return new SXSSFWorkbook(new XSSFWorkbook());
        return new SXSSFWorkbook();
    }

    private static boolean isNativeFile(String path) {
        if (path.length() < 5) {
            try {
                URI uri = new URI(path);
                if (uri.getScheme() == PoiPool.HTTP || uri.getScheme() == PoiPool.HTTPS) {
                    return true;
                }
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }
        return false;
    }

    private static InputStream uploadFile(String path){
        try {
            URL url = new URL(path);
            URLConnection conn = url.openConnection();
            //设置超时时间
            conn.setConnectTimeout(60 * 1000);
            //防止屏蔽程序抓取而返回403错误
            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
            return conn.getInputStream();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("upload timeOut or file not exists",e);
        }
    }

    //======================= cell option =================================================================
    public static Object getCellValue(Cell cell){
        if(cell == null){
            return null;
        }
        CellType cellType = cell.getCellType();
        Object result = null;
        switch (cellType){
            case _NONE:
                break;
            case STRING:
                result = cell.getStringCellValue();
                break;
            case NUMERIC:
                result = cell.getNumericCellValue();
                String str = Double.toString(cell.getNumericCellValue());
                if(str.indexOf(".") == 1 && str.indexOf("E") != -1) {
                    //防止数字太长被转换成科学计数法
                    DecimalFormat df = new DecimalFormat("0");
                    result = df.format(cell.getNumericCellValue());
                }

                // 由于日期类型格式也被认为是数值型，此处判断是否是日期的格式，若时，则读取为日期类型
                // org.apache.poi.ss.usermodel.BuiltinFormats
                if(cell.getCellStyle().getDataFormat() > 0xd)  {
                    result = cell.getDateCellValue();
                }
                break;
            case BOOLEAN:
                result = cell.getBooleanCellValue();
                break;
            case BLANK:
                result = cell.getDateCellValue();
                break;
            case FORMULA:
                break;
            case ERROR:
                break;
            default:
                break;
        }
        return  result;
    }

    //============================ reflection option ===================================

    /**
     * 反射获取值
     * @param t 实例对象
     * @param column 列名
     * @return 值
     * @throws Exception
     */
    public static <T> String getValue(T t, String column) throws Exception {
        return getValue(t, t.getClass(), column);
    }

    public static <T> String getValue(T t, Class<?> clazz, String column) throws Exception {
        Method declaredMethod = null;
        try {
            declaredMethod = clazz.getDeclaredMethod(getMethodName(column));
        } catch (NoSuchMethodException e) {
            declaredMethod = clazz.getSuperclass().getDeclaredMethod(getMethodName(column));
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        if (declaredMethod == null){
            return null;
        }
        return formatValue(declaredMethod.invoke(t)).toString();
    }

    public static String getMethodName(String methodName){
        return "get".concat(capitalize(methodName));
    }

    /**
     * 首字符转大写
     * @param str 源字符
     * @return 转大写的字符
     */
    public static String capitalize(final String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return str;
        }

        //获取第一个字节
        final char firstChar = str.charAt(0);
        //将第一个字符大写
        final char newChar = Character.toTitleCase(firstChar);
        if (firstChar == newChar) {
            // already capitalized
            return str;
        }

        char[] newChars = new char[strLen];
        newChars[0] = newChar;
        str.getChars(1,strLen, newChars, 1);
        return String.valueOf(newChars);
    }

    public static Object formatValue(Object object){
        if(object == null){
            return "";
        }
        if(object instanceof String){
            return object;
        }
        if(object instanceof Number){
            return object;
        }
        //注意 当执行导入操作时时，格式化的时间将无法转换成LocalTime,需要重新定义Date 类型进行接收
        if(object instanceof LocalDate){
            LocalDate localDate = (LocalDate) object;
            return localDate.format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN));
        }
        if(object instanceof LocalDateTime){
            LocalDateTime localDateTime = (LocalDateTime) object;
            return localDateTime.format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN));
        }
        if(object instanceof Date){
            LocalDateTime localDateTime = (LocalDateTime) object;
            return localDateTime.format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN));
        }
        return object + "";
    }
}
