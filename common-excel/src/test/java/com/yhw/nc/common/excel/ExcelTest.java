package com.yhw.nc.common.excel;

import com.yhw.nc.common.excel.impl.StandardExcelUtil;
import org.apache.commons.compress.utils.Lists;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class ExcelTest {

    public static void main(String[] args) throws Exception{
        System.out.println(export()); //200000行 多线程 13736 单线程 18518
//        importNative();
//        importRemote();
//        importRemoteMap();
    }

    private static String export(){
        List<User>  list = Lists.newArrayList();
        long start = System.currentTimeMillis();
        for(int i =0;i<200000;i++){
            User user = new User();
            user.setAge(10+i);
            user.setName("aaa"+i);
            user.setCreateTime(LocalDateTime.now());
            list.add(user);
        }
        long end = System.currentTimeMillis();
        System.out.println("生成数据花费时间：" + (end - start));

        String result = new StandardExcelUtil().exportFilePath("测试2222", list, new String[]{"用户名", "年龄", "创建时间"}, new String[]{"name", "age", "createTime"});

        System.out.println("生成Excel花费时间：" + (System.currentTimeMillis() - end));
        return result;
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
