package com.awy.common.excel.utils;

import cn.hutool.core.util.StrUtil;
import com.awy.common.excel.constants.PoiPool;
import com.awy.common.excel.enums.ExcelTypeEnum;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;

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
}
