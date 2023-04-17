package com.awy.common.excel;

import com.awy.common.excel.custom.MultiSheetUtil;
import com.awy.common.excel.custom.SimpleStyleUtil;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yhw
 * @date 2023-04-17
 */
public class CustomExcelTest {

    @Test
    public void customMultiSheetTest() {
        List<MultiSheetImportVO> vos = new ArrayList<>();
        vos.add(new MultiSheetImportVO("abc", "data", 1, "data2", 2));
        vos.add(new MultiSheetImportVO("abc1", "data11", 11, "data22", 22));
        vos.add(new MultiSheetImportVO("abc2", "data2", 2, "data22", 22));
        vos.add(new MultiSheetImportVO("abc3", "data3", 3, "data33", 33));

        String[] titles = new String[]{"编号", "明细数量", "异常天数(固定值)"};
        Integer[] widths = new Integer[]{50, 50, 50};
        MultiSheetUtil multiSheetUtil = new MultiSheetUtil();
        String uri = multiSheetUtil.exportCustomize("自定义样式测试", vos, titles, widths);
        System.out.println(uri);
        //自定义导出，可以忽略反射入参
        uri = multiSheetUtil.exportFilePath("自定义默认导出", vos, titles, new String[]{});
        System.out.println(uri);
    }

    @Test
    public void customStyle() {
        List<MultiSheetImportVO> vos = new ArrayList<>();
        SimpleStyleUtil excelUtil = new SimpleStyleUtil();
        String[] titles = new String[]{"编号", "明细数量", "异常天数(固定值)"};
        Integer[] widths = new Integer[]{50, 50, 50};


        String uri = excelUtil.exportCustomize("自定义选择框范围测试", vos, titles, widths);
        System.out.println(uri);
    }
}
