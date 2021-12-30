package com.awy.common.util.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author yhw
 * @date 2021-12-30
 */
public class NumberUtil extends cn.hutool.core.util.NumberUtil {

    /**
     * 保留有效位(向上进一位,正数变大,负数变小)
     * @param source 源数值
     * @param newScale 保留位数
     * @return 数值对象
     */
    public static BigDecimal getBigDecimalUPMode(String source,int newScale) {
        return getRoundBigDecimal(source,newScale, RoundingMode.UP);
    }

    /**
     * 保留有效位
     * 四舍五入（若舍弃部分>=.5，就进位）
     * @param source 源数值
     * @param newScale 保留位数
     * @return 数值对象
     */
    public static BigDecimal getBigDecimalHALF_UPMode(String source,int newScale) {
        return getRoundBigDecimal(source,newScale, RoundingMode.HALF_UP);
    }

    /**
     * 保留有效位
     * 五舍六入（若舍弃部分 > .5，就进位）
     * @param source 源数值
     * @param newScale 保留位数
     * @return 数值对象
     */
    public static BigDecimal getBigDecimalHALF_DOWNMode(String source,int newScale) {
        return getRoundBigDecimal(source,newScale, RoundingMode.HALF_DOWN);
    }

    /**
     * 保留有效位(截断)
     * @param source 源数值
     * @param newScale 保留位数
     * @return 数值对象
     */
    public static BigDecimal getBigDecimalDOWNMode(String source,int newScale) {
        return getRoundBigDecimal(source,newScale, RoundingMode.DOWN);
    }

    /**
     * 保留有效位(向上进一位)
     * 天板(正数变大,负数变大)
     * @param source 源数值
     * @param newScale 保留位数
     * @return 数值对象
     */
    public static BigDecimal getBigDecimalCEILINGMode(String source,int newScale) {
        return getRoundBigDecimal(source,newScale, RoundingMode.CEILING);
    }

    /**
     *  保留有效位
     *  地板(正数变小,负数变小)
     * @param source 源数值
     * @param newScale 保留位数
     * @return 数值对象
     */
    public static BigDecimal getBigDecimalFLOORMode(String source,int newScale) {
        return getRoundBigDecimal(source,newScale, RoundingMode.FLOOR);
    }

    private static BigDecimal getRoundBigDecimal(String source,int newScale,RoundingMode mode) {
        return new BigDecimal(source).setScale(newScale, mode);
    }

}
