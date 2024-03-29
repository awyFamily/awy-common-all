package com.awy.common.excel;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;
import com.awy.common.excel.constants.PoiPool;
import com.awy.common.excel.enums.ExcelTypeEnum;
import com.awy.common.util.utils.FileUtil;
import lombok.Data;
import org.apache.commons.compress.utils.Lists;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * When you need to implement complex operations,
 * Inherit the current class implementation abstract method
 * reference : http://poi.apache.org/components/spreadsheet/quick-guide.html
 * @author yhw
 */
@Data
public abstract class AbstractExcelUtil<T>{

    //====================================================== import =================================================================

    //----------------------------- import get map list -------------------------------------------------

    /**
     * 读取数据
     * @param workbook 工作薄
     * @param columns 数组列  对应 列下标
     * @return map
     */
    protected abstract List<Map<String,Object>> readData(Workbook workbook, String[] columns);

    /**
     * 通过url导入数据
     * @param networkPath
     * @param columns
     * @return
     */
    public List<Map<String,Object>> importRemote(String networkPath,String[] columns) {
        return readData(getImportWorkbook(networkPath, PoiPool.NOT_NATIVE_FILE, getFileSuffix(networkPath)), columns);
    }

    /**
     * 通过本地文件路径导入数据
     * @param path
     * @param columns
     * @return
     */
    public List<Map<String,Object>> importNative(String path,String[] columns){
        return readData(getImportWorkbook(path,PoiPool.IS_NATIVE_FILE,getFileSuffix(path)),columns);
    }

    /**
     * 通过输入流导入数据
     * @param inputStream 数据流
     * @param fileName 文件名称
     * @param columns 列名
     * @return 数据列表
     */
    public List<Map<String,Object>> importStream(InputStream inputStream,String fileName,String[] columns) {
        return readData(getImportWorkbook(inputStream, getFileSuffix(fileName)), columns);
    }

    //--------------------------- import get object list ---------------------------------------

    /**
     * 读取数据
     * @param workbook 工作薄
     * @param columns 数组列  对应 列下标
     * @param clazz 类名
     * @return T 列表
     */
    protected abstract List<T> readData(Workbook workbook, String[] columns,Class<T> clazz);

    /**
     * 获取远程导入数据
     * @param networkPath 远程文件地址
     * @param columns 需要导出的列名
     * @param clazz 类名
     * @return T 列表
     */
    public List<T> importRemote(String networkPath,String[] columns,Class<T> clazz) {
//        return getResultList(importRemote(networkPath,columns),clazz);
        return readData(getImportWorkbook(networkPath, PoiPool.NOT_NATIVE_FILE, getFileSuffix(networkPath)),columns,clazz);
    }

    /**
     * 获取本地导入数据
     * @param path 本地文件路径
     * @param columns 需要导出的列名
     * @param clazz 对象名
     * @return T 列表
     */
    public List<T> importNative(String path,String[] columns,Class<T> clazz){
//        return getResultList(importNative(path,columns),clazz);
        return readData(getImportWorkbook(path, PoiPool.IS_NATIVE_FILE, getFileSuffix(path)),columns,clazz);
    }

    /**
     * 输入流导入数据
     * @param inputStream 本地文件路径
     * @param fileName 文件名称
     * @param columns 需要导出的列名
     * @param clazz 对象名
     * @return T 列表
     */
    public List<T> importStream(InputStream inputStream,String fileName,String[] columns,Class<T> clazz){
//        return getResultList(importNative(path,columns),clazz);
        return readData(getImportWorkbook(inputStream, getFileSuffix(fileName)),columns,clazz);
    }



    //================================== import common option =======================================================

    private InputStream uploadFile(String path){
        try {
            URL url = new URL(path);
            URLConnection conn = url.openConnection();
            //设置超时时间
            conn.setConnectTimeout(60*1000);
            //防止屏蔽程序抓取而返回403错误
            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
            return conn.getInputStream();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("upload timeOut or file not exists",e);
        }
    }

    private ExcelTypeEnum getFileSuffix(String path){
        if(StrUtil.isEmpty(path)){
            throw new RuntimeException("file path is empty ...");
        }
        if(path.endsWith(PoiPool.XSSF_WORK_BOOK)){
            return ExcelTypeEnum.XSSF_WORK_BOOK;
        }
        if(path.endsWith(PoiPool.HSSF_WORK_BOOK)){
            return ExcelTypeEnum.HSSF_WORK_BOOK;
        }
        throw new RuntimeException("file type error ...");
    }

    private Workbook getImportWorkbook(String path,Boolean isNativeFile,ExcelTypeEnum typeEnum){
        try(InputStream inputStream = isNativeFile ? new FileInputStream(path) : uploadFile(path)){
            /*switch (typeEnum){
                case XSSF_WORK_BOOK:
                    workbook = createXssfWorkbook(inputStream);
                    break;
                case HSSF_WORK_BOOK:
                    workbook = createHssfWorkbook(inputStream);
                    break;
                default:
                    throw new RuntimeException("excel type not exists");
            }*/
            return this.getImportWorkbook(inputStream,typeEnum);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    private Workbook getImportWorkbook(InputStream inputStream,ExcelTypeEnum typeEnum){
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

    private List<T> getResultList(List<Map<String, Object>> maps,Class<T> clazz){
        if(CollectionUtil.isEmpty(maps)){
            return Lists.newArrayList();
        }
        return maps.stream().map(obj->{
            return  BeanUtil.mapToBean(obj,clazz,true);
        }).collect(Collectors.toList());
    }

    //======================= cell option =================================================================
    public Object getCellValue(Cell cell){
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

    //================================================ export ===============================================

    /**
     * 导出写入数据（一个sheet）
     * @param sheet sheet
     * @param style 列样式
     * @param datas 数据列表
     * @param columns 需要导出的列
     */
    protected abstract void writeData(Sheet sheet, CellStyle style, List<T> datas, String[] columns);

    /**
     * 自定义样式导出
     * @param workbook 工作薄
     * @param datas 数据列表
     * @param titles 表头
     * @param widths 宽度列表
     */
    protected abstract void writeCustomizeData(Workbook workbook,List<T> datas,String[] titles,Integer[] widths);


    //============================= export single sheet option =========================================

    /**
     * 获取导出字节数组
     * @param datas 数据
     * @param titles 表头
     * @param columns 导出列
     * @return excel 字节数组
     */
    public byte[]  exportBytes(List<T> datas, String[] titles, String[] columns){
        return exportBytes(datas,null,titles,columns);
    }

    public byte[]  exportBytes(List<T> datas,CellStyle style, String[] titles, String[] columns){
        return exportBytes(datas,style,titles,columns,null);
    }

    public byte[]  exportBytes(List<T> datas, CellStyle style,  String[] titles, String[] columns,Integer[] widths){
        return exportBytes("mySheet",datas,style,titles,columns,widths);
    }

    public byte[] exportBytes(String sheetName, List<T> datas, CellStyle style, String[] titles, String[] columns){
        return exportBytes(sheetName,datas,style,titles,columns,null);
    }

    public byte[] exportBytes(String sheetName, List<T> datas, CellStyle style, String[] titles, String[] columns, Integer[] widths){
        return exportBytes(sheetName,datas,style,PoiPool.DEFAULT_PAGE_SIZE,titles,columns,widths);
    }

    public byte[] exportBytes(String sheetName, List<T> datas, CellStyle style,int sheetPageSize, String[] titles, String[] columns, Integer[] widths){
        Workbook workbook = getWorkbook(datas, sheetName, style, sheetPageSize , titles, columns,  widths);

        try(ByteArrayOutputStream outputStream = new ByteArrayOutputStream()){
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            close(workbook);
        }
        return null;
    }


    /**
     * 获取导出文件路径
     * @param excelName excel 文件名称
     * @param datas 数据列表
     * @param titles 表头
     * @param columns 列
     * @return 文件路径
     */
    public String  exportFilePath(String excelName, List<T> datas, String[] titles, String[] columns){
        return exportFilePath(excelName,datas,excelName,titles,columns);
    }

    public String  exportFilePath(String excelName, List<T> datas, String sheetName, String[] titles, String[] columns){
        return exportFilePath(excelName,datas,sheetName,titles,columns,null);
    }

    public String  exportFilePath(String excelName, List<T> datas, CellStyle style, String[] titles, String[] columns){
        return exportFilePath(excelName,datas,style,excelName,titles,columns);
    }

    public String  exportFilePath(String excelName, List<T> datas,CellStyle style, String sheetName, String[] titles, String[] columns){
        return exportFilePath(excelName,datas,sheetName,style,titles,columns,null);
    }

    public String  exportFilePath(String excelName, List<T> datas, String sheetName, String[] titles, String[] columns, Integer[] widths){
        return exportFilePath(excelName,datas,sheetName,null,titles,columns,widths);
    }

    public String  exportFilePath(String excelName, List<T> datas, String sheetName, CellStyle style, String[] titles, String[] columns, Integer[] widths){
        return exportFilePath(excelName,datas,sheetName,style,PoiPool.DEFAULT_PAGE_SIZE,titles,columns,widths);
    }

    public String  exportFilePath(String excelName, List<T> datas, String sheetName, CellStyle style,int sheetPageSize, String[] titles, String[] columns, Integer[] widths){
        return this.exportFilePath(excelName,PoiPool.EXPORT_EXCEL_PATH,datas,sheetName,style,sheetPageSize,titles,columns,widths);
    }

    public String  exportFilePath(String excelName, String folderName,List<T> datas, String sheetName, CellStyle style,int sheetPageSize, String[] titles, String[] columns, Integer[] widths){
        if(sheetPageSize > PoiPool.DEFAULT_PAGE_SIZE){
            sheetPageSize = PoiPool.DEFAULT_PAGE_SIZE;
        }
        Workbook workbook = getWorkbook(datas, sheetName, style, sheetPageSize, titles, columns,  widths);
        return getExportHomePath(excelName,folderName,workbook);
    }

    public String  exportFilePathByFolderPath(String excelName, String folderPath,List<T> datas, String sheetName, CellStyle style,int sheetPageSize, String[] titles, String[] columns, Integer[] widths){
        if(sheetPageSize > PoiPool.DEFAULT_PAGE_SIZE){
            sheetPageSize = PoiPool.DEFAULT_PAGE_SIZE;
        }
        Workbook workbook = getWorkbook(datas, sheetName, style, sheetPageSize, titles, columns,  widths);
        return getExportPath(excelName,folderPath,workbook);
    }

    public File exportZipFile(String excelName,String folderPath, List<T> datas,   String[] titles, String[] columns, Integer[] widths){
        return this.exportZipFile(excelName,folderPath,datas,"mySheet",titles,columns,widths);
    }

    public File exportZipFile(String excelName,String folderPath, List<T> datas, String sheetName,  String[] titles, String[] columns, Integer[] widths){
        return this.exportZipFile(excelName,folderPath,datas,sheetName,null,PoiPool.DEFAULT_SINGLE_FILE_ROWS,titles,columns,widths);
    }

    public File exportZipFile(String excelName,String folderPath, List<T> datas, String sheetName, CellStyle style,int singleFileRowSize, String[] titles, String[] columns, Integer[] widths){
        if(singleFileRowSize > PoiPool.DEFAULT_SINGLE_FILE_ROWS){
            singleFileRowSize = PoiPool.DEFAULT_SINGLE_FILE_ROWS;
        }
        int size = datas.size();
        if(size <= singleFileRowSize){
            this.exportFilePathByFolderPath(excelName, folderPath, datas, sheetName, style, singleFileRowSize, titles, columns, widths);
        }else {
            int page = this.getPages(size,singleFileRowSize);
            //使用线程池处理
            ThreadPoolExecutor batchExportJobPool = this.getThreadPoolExecutor();

            List<CompletableFuture<String>> list = Lists.newArrayList();
            List<T> ts;

            AtomicInteger atomicInteger = new AtomicInteger();
            for(int i = 1; i <= page ; i++){
                if(i == 1){
                    ts = datas.subList(0, singleFileRowSize);
                    list.add(this.AsyncExportPath(excelName,folderPath,ts, sheetName, style, singleFileRowSize, titles, columns, widths,batchExportJobPool));
                }else if(i == page){
                    ts = datas.subList(singleFileRowSize * ( i - 1 ), size);
                    list.add(this.AsyncExportPath(this.getCurrentAtomicName(excelName,atomicInteger),folderPath,ts, sheetName, style, singleFileRowSize, titles, columns, widths,batchExportJobPool));
                }else {
                    ts = datas.subList(singleFileRowSize * ( i - 1 ), singleFileRowSize * i);
                    list.add(this.AsyncExportPath(this.getCurrentAtomicName(excelName,atomicInteger),folderPath,ts, sheetName, style, singleFileRowSize, titles, columns, widths,batchExportJobPool));
                }
            }

            try {
                for (CompletableFuture<String> result : list){
                    result.get(120,TimeUnit.SECONDS);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            batchExportJobPool.shutdown();
        }
//        String zipPath = folderPath.concat(File.separator).concat(excelName).concat(".zip");
        return ZipUtil.zip(folderPath);
//        return ZipUtil.zip(folderPath,zipPath);
    }

    public void deleteZip(String folderPath,File zipFile){
        CompletableFuture.runAsync(() -> {
            FileUtil.delAllFile(folderPath);
            File file = new File(folderPath);
            if (file.exists()) {
                file.delete();
            }
            if(zipFile.exists()){
                zipFile.delete();
            }
        });
    }


    /**
     * 获取 Workbook
     * @param datas 数据
     * @param sheetName sheetName
     * @param titles 表头名称
     * @param columns 列
     * @param widths 宽
     * @return Workbook
     */
    public Workbook  exportWorkbook(List<T> datas, String sheetName, String[] titles, String[] columns, Integer[] widths){
        return getWorkbook(datas, sheetName, null, PoiPool.DEFAULT_PAGE_SIZE, titles, columns,  widths);
    }

    private Workbook getWorkbook( List<T> datas, String sheetName,CellStyle style,int sheetPageSize, String[] titles, String[] columns, Integer[] widths){
        Workbook workbook = createSXssfWorkbook();
        if(style == null){
            style = getDefaultCellStyle(workbook);
        }

        int size = datas.size();
        if(size <= sheetPageSize){
            generateSheet(workbook,sheetName,datas,style,titles,columns,widths);
        }else {
            final CellStyle fStyle = style;
            //计算总页码
            /*int page = size / sheetPageSize;
            page =  size % sheetPageSize == 0 ? page : ++page;*/
            int page = this.getPages(size,sheetPageSize);

            //使用线程池处理
            ThreadPoolExecutor batchExportJobPool = this.getThreadPoolExecutor();

            List<CompletableFuture<Void>> list = Lists.newArrayList();
            List<T> ts;

            AtomicInteger atomicInteger = new AtomicInteger();
            for(int i = 1; i <= page ; i++){
                if(i == 1){
                    ts = datas.subList(0, sheetPageSize);
                    list.add(AsyncExport(workbook,sheetName,ts,fStyle,titles,columns,widths,batchExportJobPool));
                }else if(i == page){
                    ts = datas.subList(sheetPageSize * ( i - 1 ), size);
                    list.add(AsyncExport(workbook,getCurrentAtomicName(sheetName,atomicInteger),ts,fStyle,titles,columns,widths,batchExportJobPool));
                }else {
                    ts = datas.subList(sheetPageSize * ( i - 1 ), sheetPageSize * i);
                    list.add(AsyncExport(workbook,getCurrentAtomicName(sheetName,atomicInteger),ts,fStyle,titles,columns,widths,batchExportJobPool));
                }
            }

            try {
                for (CompletableFuture result : list){
                    result.get(60,TimeUnit.SECONDS);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            batchExportJobPool.shutdown();
        }
        return workbook;
    }

    private String getCurrentAtomicName(String name,AtomicInteger atomicInteger){
        return name.concat("-").concat(String.valueOf(atomicInteger.incrementAndGet()));
    }


    private CompletableFuture<Void> AsyncExport(Workbook workbook,String sheetName,List<T> datas,CellStyle style, String[] titles, String[] columns, Integer[] widths,ThreadPoolExecutor batchExportJobPool){
        return CompletableFuture.runAsync(() -> {
            generateSheet(workbook,sheetName,datas,style,titles,columns,widths);
        }, batchExportJobPool);
    }

    private CompletableFuture<String> AsyncExportPath(String excelName, String folderName,List<T> datas, String sheetName, CellStyle style,int singleFileRowSize, String[] titles, String[] columns, Integer[] widths,ThreadPoolExecutor batchExportJobPool){
        return CompletableFuture.supplyAsync(() -> {
            return exportFilePathByFolderPath(excelName,folderName,datas, sheetName, style, singleFileRowSize, titles, columns, widths);
        }, batchExportJobPool);
    }


    private void generateSheet(Workbook wb, String sheetName, List<T> datas, CellStyle style,String[] titles, String[] columns, Integer[] widths){
        Sheet sheet = createSheet(wb, sheetName);
        if(widths != null && widths.length > 0){
            setColumnWidth(sheet,widths);
        }
        createTitleHead(sheet,style,titles);
        writeData(sheet,style,datas,columns);
    }

    //=================================================== export multi sheet option =====================================================================

    public String  exportCustomize(String excelName, List<T> datas, String[] titles, Integer[] widths){
        Workbook  workbook = createSXssfWorkbook();

        writeCustomizeData(workbook,datas,titles,widths);
        return getExportHomePath(excelName,PoiPool.EXPORT_EXCEL_PATH,workbook);
    }

    //=============================================================================================================================================

    private int getPages(int size,int pageSize){
        //计算总页码
        int page = size / pageSize;
        return   size % pageSize == 0 ? page : ++page;
    }

    private ThreadPoolExecutor getThreadPoolExecutor(){
        return new ThreadPoolExecutor(
                1,
                10,
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(2000),
                new ThreadFactory() {
                    @Override
                    public Thread newThread(Runnable r) {
                        return new Thread(r, "excel batch export job-" + r.hashCode());
                    }
                });
    }

    private String getExportHomePath(String excelName,String folderName,Workbook  workbook){
        //获取文件路径
        String excelPath = getXlsxHomeFilePath(excelName,folderName);
        try(OutputStream outputStream = new FileOutputStream(new File(excelPath))){
            workbook.write(outputStream);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            close(workbook);
        }
        return excelPath;
    }

    private String getExportPath(String excelName,String folderPath,Workbook  workbook){
        //获取文件路径
        String excelPath = folderPath.concat(File.separator).concat(excelName).concat(PoiPool.XSSF_WORK_BOOK);
        try(OutputStream outputStream = new FileOutputStream(new File(excelPath))){
            workbook.write(outputStream);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            close(workbook);
        }
        return excelPath;
    }

    /**
     * 获取 xlsx 文件地址
     * @param name 文件名称
     * @param folderName 文件夹名称
     * @return 文件地址
     */
    private String getXlsxHomeFilePath(String name,String folderName){
        SimpleDateFormat df = new SimpleDateFormat(DatePattern.PURE_DATETIME_PATTERN);
        String fileName = name.concat(df.format(new Date())).concat(PoiPool.XSSF_WORK_BOOK);
        return FileUtil.getCurrentDataFormatFilePath(folderName,fileName);
    }

    /**
     * 创建表头(标题栏位)
     * @param sheet
     * @param style 样式
     * @param titles 头内容
     */
    public void createTitleHead(Sheet sheet, CellStyle style, String[] titles){
        Row row = createXSSFRow(sheet, 0);
        Cell cell;
        for(int i = 0; i < titles.length; i++){
            cell = row.createCell(i, CellType.STRING);
            cell.setCellValue(titles[i]);
            cell.setCellStyle(style);
        }
    }

    //====================== create table option ==================================================

    /**
     * 创建以【.xlsx】为后缀的表格(只写, Stream 的方式)
     * @return xlsx table
     */
    public Workbook createSXssfWorkbook(){
        return new SXSSFWorkbook(new XSSFWorkbook());
//        return new XSSFWorkbook();
    }

    /**
     * 读取Excel
     * @param inputStream 流
     * @return workBook
     */
    public Workbook createXssfWorkbook(InputStream inputStream){
        try {
            return new XSSFWorkbook(inputStream);
            ////SXSSFSheet -> _xFromSxHash(读取的文件不可以读取行)  , _sxFromXHash(读取的文件可以读取行)
            //return new SXSSFWorkbook(new XSSFWorkbook(inputStream));
        } catch (IOException e) {
            throw new RuntimeException("file type illegal",e);
        }
    }

    /**
     * 创建以【.xls】为后缀的表格
     * @return xls table
     */
    public Workbook createHssfWorkbook(){
        return new HSSFWorkbook();
    }

    public Workbook createHssfWorkbook(InputStream inputStream){
        try {
            return new HSSFWorkbook(inputStream);
        } catch (IOException e) {
            throw new RuntimeException("file type illegal",e);
        }
    }


    public Sheet createSheet(Workbook wb,String sheetName){
        return wb.createSheet(sheetName);
    }

    public Row createXSSFRow(Sheet sheet, int rownum){
        return sheet.createRow(rownum);
    }

    //===================================== style option =====================================

    public void setColumnWidth(Sheet sheet,Integer[] widths){
        //自动宽度  sheet.autoSizeColumn()
        for(int i = 0; i < widths.length; i++){
            //设置列宽
            sheet.setColumnWidth(i,getStandardWidth(widths[i]));
        }
    }

    private int getStandardWidth(Integer width){
        return (int)((width + 0.72) * 256);
    }

    private CellStyle getDefaultCellStyle(Workbook wb){
        return getCellStyle(wb,IndexedColors.GREEN,HorizontalAlignment.CENTER,VerticalAlignment.CENTER);
    }

    /**
     * 创建一个单元格并以某种方式对齐它。
     * @param wb 表格
     * @param colors 背景颜色
     * @param halign 单元格的水平对齐方式。
     * @param valign 单元格的垂直对齐方式
     * @return 列样式
     */
    public CellStyle getCellStyle(Workbook wb, IndexedColors colors, HorizontalAlignment halign, VerticalAlignment valign){
        CellStyle cellStyle = wb.createCellStyle();
        cellStyle.setFillBackgroundColor(colors.index);
        cellStyle.setAlignment(halign);
        cellStyle.setVerticalAlignment(valign);
        return cellStyle;
    }

    public  void setCellGBKValue(Cell cell, String value) {
        setCellGBKValue(null,cell,value);
    }

    public  void setCellGBKValue(CellStyle style, Cell cell, String value) {
        if(StrUtil.isNotBlank(value)&&!"null".equals(value)) {
            cell.setCellValue(value);
        }
        if(style != null){
            cell.setCellStyle(style);
        }
    }

    public void close(Workbook workbook){
        try {
            if (workbook instanceof SXSSFWorkbook) {
                ((SXSSFWorkbook) workbook).dispose();
            }
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //============================ reflection option ===================================

    /**
     * 反射获取值
     * @param t 实例对象
     * @param column 列名
     * @return 值
     * @throws Exception
     */
    protected String getValue(T t, String column) throws Exception{
        Method declaredMethod = null;
        Class<?> clazz = t.getClass();
        try {
            declaredMethod = clazz.getDeclaredMethod(getMethodName(column));
        } catch (NoSuchMethodException e) {
            declaredMethod = clazz.getSuperclass().getDeclaredMethod(getMethodName(column));
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        if(declaredMethod == null){
            return null;
        }
        return formatValue(declaredMethod.invoke(t)).toString();
    }

    public String getMethodName(String methodName){
        return "get".concat(capitalize(methodName));
    }


    /**
     * 首字符转大写
     * @param str 源字符
     * @return 转大写的字符
     */
    private static String capitalize(final String str) {
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

    private Object formatValue(Object object){
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
        return object+"";
    }
}
