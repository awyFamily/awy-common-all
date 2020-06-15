package com.yhw.nc.common.utils;

import cn.hutool.core.date.DateException;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.format.DateParser;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class DateJdK8Util {

    /**
     * 转换LocalDate
     * @param dateStr 年月日字符串
     * @return LocalDate
     */
    public  static LocalDate parseLocalDate(String dateStr){
        if (StrUtil.isBlank(dateStr)) {
            return null;
        } else {
            int length = dateStr.length();
            if (length == "yyyyMMdd".length()) {
                return parseLocalDate(dateStr, DatePattern.PURE_DATE_FORMAT);
            }else if (length == "yyyy-MM-dd".length()) {
                return parseLocalDate(dateStr,DatePattern.NORM_DATE_FORMAT);
            }
            throw new DateException("No format fit for date String [{}] !", new Object[]{dateStr});
        }
    }

    private   static LocalDate parseLocalDate(String dateStr,DateParser parser){
        return LocalDate.parse(dateStr,DateTimeFormatter.ofPattern(parser.getPattern()));
    }

    /**
     * 转换LocalDateTime
     * @param dateStr 时间字符串
     * @return LocalDateTime
     */
    public  static LocalDateTime parseLocalDateTime(String dateStr){
        if (StrUtil.isBlank(dateStr)) {
            return null;
        } else {
            dateStr = StrUtil.removeAll(dateStr.trim(), new char[]{'日', '秒'});
            int length = dateStr.length();
            if (NumberUtil.isNumber(dateStr)) {
                if (length == "yyyyMMddHHmmss".length()) {
                    return parseLocalDateTime(dateStr, DatePattern.PURE_DATETIME_FORMAT);
                }
                if (length == "yyyyMMddHHmmssSSS".length()) {
                    return parseLocalDateTime(dateStr, DatePattern.PURE_DATETIME_MS_FORMAT);
                }
            }

            if (length == "yyyy-MM-dd HH:mm:ss".length()) {
                return parseLocalDateTime(dateStr,DatePattern.NORM_DATETIME_FORMAT);
            }  else if (length == "yyyy-MM-dd HH:mm".length()) {
                return parseLocalDateTime(dateStr, (DateParser)DatePattern.NORM_DATETIME_MINUTE_FORMAT);
            } else if (length >= "yyyy-MM-dd HH:mm:ss.SSS".length() - 2) {
                return parseLocalDateTime(dateStr, (DateParser)DatePattern.NORM_DATETIME_MS_FORMAT);
            } else {
                throw new DateException("No format fit for date String [{}] !", new Object[]{dateStr});
            }
        }
    }

    private  static LocalDateTime parseLocalDateTime(String dateStr,DateParser parser){
        return LocalDateTime.parse(dateStr, DateTimeFormatter.ofPattern(parser.getPattern()));
    }

    //=========================================  format ============================================

    /**
     * 将LocalDate 格式化字符串
     * @param localDate LocalDate
     * @return  yyyy-MM-dd 格式
     */
    public static String formatLocalDate(LocalDate localDate){
        if(localDate == null){
            return "";
        }
        return formatLocalDate(localDate,DatePattern.NORM_DATE_FORMAT);
    }

    /**
     * 将LocalDate 格式化字符串
     * @param localDate LocalDate
     * @param formatter 格式化类型
     * @return formatter 格式时间
     */
    public static String formatLocalDate(LocalDate localDate,String formatter){
        if(localDate == null){
            return "";
        }
        if (StrUtil.isBlank(formatter)) {
            return formatLocalDate(localDate,DatePattern.NORM_DATE_FORMAT);
        }
        int length = formatter.length();
        if (length == "yyyyMMdd".length()) {
            return formatLocalDate(localDate,DatePattern.PURE_DATE_PATTERN);
        }else if(length == "yyyy-MM-dd".length()){
            return formatLocalDate(localDate,DatePattern.NORM_DATE_FORMAT);
        }
        return formatLocalDate(localDate,DatePattern.NORM_DATE_FORMAT);
    }

    private static String formatLocalDate(LocalDate localDate,DateParser parser){
        return localDate.format(DateTimeFormatter.ofPattern(parser.getPattern()));
    }


    /**
     * 将LocalDateTime 格式化字符串
     * @param localDateTime localDateTime
     * @return yyyy-MM-dd HH:mm:ss 格式
     */
    public static String formatLocalDateTime(LocalDateTime localDateTime){
        if(localDateTime == null){
            return "";
        }
        return formatLocalDateTime(localDateTime,DatePattern.NORM_DATETIME_FORMAT);
    }

    /**
     *  将LocalDateTime 格式化字符串
     * @param localDateTime localDateTime
     * @param formatter 格式化类型
     * @return formatter 格式化时间
     */
    public static String formatLocalDateTime(LocalDateTime localDateTime,String formatter){
        if(localDateTime == null){
            return "";
        }
        if (StrUtil.isBlank(formatter)) {
            return formatLocalDateTime(localDateTime,DatePattern.NORM_DATETIME_FORMAT);
        }

        int length = formatter.length();
        if (NumberUtil.isNumber(formatter)) {
            if (length == "yyyyMMddHHmmss".length()) {
                return formatLocalDateTime(localDateTime, DatePattern.PURE_DATETIME_FORMAT);
            }
            if (length == "yyyyMMddHHmmssSSS".length()) {
                return formatLocalDateTime(localDateTime, DatePattern.PURE_DATETIME_MS_FORMAT);
            }
        }

        if (length == "yyyy-MM-dd HH:mm:ss".length()) {
            return formatLocalDateTime(localDateTime,DatePattern.NORM_DATETIME_FORMAT);
        }  else if (length == "yyyy-MM-dd HH:mm".length()) {
            return formatLocalDateTime(localDateTime, (DateParser)DatePattern.NORM_DATETIME_MINUTE_FORMAT);
        } else if (length >= "yyyy-MM-dd HH:mm:ss.SSS".length() - 2) {
            return formatLocalDateTime(localDateTime, (DateParser)DatePattern.NORM_DATETIME_MS_FORMAT);
        }

        return formatLocalDateTime(localDateTime,DatePattern.NORM_DATETIME_FORMAT);
    }

    private static String formatLocalDateTime(LocalDateTime localDateTime,DateParser parser){
        return localDateTime.format(DateTimeFormatter.ofPattern(parser.getPattern()));
    }
}
