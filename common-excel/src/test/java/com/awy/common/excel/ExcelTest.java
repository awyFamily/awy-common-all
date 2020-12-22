package com.awy.common.excel;

import com.awy.common.excel.impl.StandardExcelUtil;
import com.awy.common.util.utils.FileUtil;
import org.apache.commons.compress.utils.Lists;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ExcelTest {

    public static void main(String[] args) throws Exception{
        System.out.println(exportZip()); //200000行 多线程 13736 单线程 18518
//        importNative();
//        importRemote();
//        importRemoteMap();

        //大数据的导出可以通过 ThreadPoolExecutor + CompletableFuture  多线程处理

        //本地文件路径
        /*String localPath = excelUtil.exportFilePath("监测记录数据",listData,"监测记录",headers,cols,widths);
                response.setHeader("content-disposition",
                        "attachment;fileName="+ URLEncoder.encode("监测记录数据.xlsx", "UTF-8"));
                InputStream is = new FileInputStream(localPath);
                response.getOutputStream().write(is.readAllBytes());
                response.getOutputStream().flush();
                is.close();
                response.getOutputStream().close();*/

        //写入response
        /*Workbook wb = excelUtil.exportWorkbook(listData, "监测记录", headers, cols, widths);
                response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
                response.setHeader("Content-Disposition", "attachment;filename="+URLEncoder.encode("监测记录数据.xlsx", "utf-8"));
                response.flushBuffer();
                wb.write(response.getOutputStream());
                excelUtil.close(wb);*/
    }

    private static String exportZip(){
        List<User>  list = getData();
        long start = System.currentTimeMillis();
        String testExport = FileUtil.createHomeFolder("testExport");
        File zipFile = new StandardExcelUtil().exportZipFile("测试",testExport, list, new String[]{"用户名", "年龄", "创建时间"}, new String[]{"name", "age", "createTime"},null);

        System.out.println("生成Excel花费时间：" + (System.currentTimeMillis() - start));
        System.out.println(zipFile.getAbsolutePath());
        new StandardExcelUtil().deleteZip(testExport,zipFile);
        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "";
    }


    private static String export(){
        List<User>  list = getData();
        long start = System.currentTimeMillis();
        /*for(int i =0;i<300000;i++){
            User user = new User();
            user.setAge(10+i);
            user.setName("aaa"+i);
            user.setCreateTime(LocalDateTime.now());
            list.add(user);
        }
        long end = System.currentTimeMillis();
        System.out.println("生成数据花费时间：" + (end - start));*/

        String result = new StandardExcelUtil().exportFilePath("测试2222", list, new String[]{"用户名", "年龄", "创建时间"}, new String[]{"name", "age", "createTime"});

        System.out.println("生成Excel花费时间：" + (System.currentTimeMillis() - start));
        return result;
    }

    private static List<User> getData(){
        List<User>  list = Lists.newArrayList();
        for(int i =0;i<300000;i++){
            User user = new User();
            user.setAge(10+i);
            user.setName("aaa"+i);
            user.setCreateTime(LocalDateTime.now());
            list.add(user);
        }
        return list;
    }

    /**
     * 注意 当执行导入操作时时，格式化的时间将无法转换成LocalTime,需要重新定义Date 类型进行接收
     * 如果没有进行格式化，则可以按照原类型接收
     */
    private static void importNative(){
        String[] columns = new String[]{"name", "age", "createTime"};
//        List<User> users = new StandardExcelUtil<User>().importNative("C:/Users/Administrator/Desktop/excelExport/20190920/测试22220190920093738.xlsx", columns, User.class);
        List<UserVO> users = new StandardExcelUtil<UserVO>().importNative("C:/Users/Administrator/Desktop/excelExport/20190920/测试222220190920101839.xlsx", columns, UserVO.class);
        users.stream().forEach(obj -> System.out.println(obj.toString()));
    }

    private static void importRemote(){
        String[] columns = new String[]{"name", "age", "createTime"};
        List<UserVO> users = new StandardExcelUtil<UserVO>().importRemote("https://asbucket1.oss-cn-beijing.aliyuncs.com/zip/%E6%B5%8B%E8%AF%95222220190920101839.xlsx", columns, UserVO.class);
        users.stream().forEach(obj -> System.out.println(obj.toString()));

    }

    private static void importRemoteMap(){
        String[] columns = new String[]{"name", "age", "createTime"};
        List<Map<String, Object>> maps = new StandardExcelUtil<UserVO>().importRemote("https://asbucket1.oss-cn-beijing.aliyuncs.com/zip/%E6%B5%8B%E8%AF%95222220190920101839.xlsx", columns);
        maps.stream().forEach(obj -> System.out.println(obj.toString()));

    }
}
