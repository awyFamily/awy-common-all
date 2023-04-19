package com.awy.common.excel.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 内置格式枚举
 * DataFormat dataFormat = creationHelper.createDataFormat();
 * @see org.apache.poi.ss.usermodel.BuiltinFormats
 * @author 叶红伟
 * @date 2023-04-18
 */
@Getter
@AllArgsConstructor
public enum BuiltinFormatsEnum {
    General(0, "General", "默认格式，根据数据类型自动识别格式"),
    integer(1, "0", "整数格式，将数字格式化为不带小数点的整数"),
    float_2(2, "0.00", "数字格式，将数字格式化为两位小数点的数字"),
    thousand(3, "#,##0", "千分位数字格式，将数字格式化为有千分位分隔符的整数"),
    thousand_float(4, "#,##0.00", "千分位数字格式，将数字格式化为有千分位分隔符的整数,带两位小数"),

    currency_$(5, "$#,##0_);($#,##0)", "货币格式，将数字格式化为美元货币格式，负数用括号括起来"),
    currency_$_2(6, "$#,##0_);[Red]($#,##0)", "货币格式，将数字格式化为美元货币格式，负数用红色显示，没有括号"),
    currency_$_3(7, "$#,##0.00);($#,##0.00)", "货币格式，将数字格式化为美元货币格式，带两位小数点，负数用括号括起来"),
    currency_$_4(8, "$#,##0.00_);[Red]($#,##0.00)", "货币格式，将数字格式化为美元货币格式，带两位小数点，负数用红色显示，没有括号"),
    percentage(9, "0%", "百分比格式，将数字乘以100后格式化为整数百分比"),

    percentage_2(0xa, "0.00%", "百分比格式，将数字乘以100后格式化为带两位小数点的百分比"),
    scientific_notation_float(0xb, "0.00E+00", "科学计数法格式，将数字格式化为带两位小数点的科学计数法"),
    fractional(0xc, "#  ?/?", "分数格式，将数字格式化为分数形式，分子和分母用问号代替"),
    fractional_2(0xd, "#  ??/??", "分数格式，将数字格式化为分数形式，分子和分母用问号代替，带空格"),
    date_m_d_yy(0xe, "m/d/yy", "日期格式，将数字格式化为月/日/年形式的日期"),

    date_d_mmm_yy(0xf, "d-mmm-yy", "日期格式，将数字格式化为日-月-年（缩写）形式的日期"),
    date_d_mmm(0x10, "d-mmm", "日期格式，将数字格式化为日-月（缩写）形式的日期"),
    date_mmm_yy(0x11, "mmm-yy", "日期格式，将数字格式化为月-年（缩写）形式的日期"),
    date_h_mm_am_pm(0x12, "h:mm  AM/PM", "时间格式，将数字格式化为12小时制的时间（带上午/下午标识）"),
    date_h_mm_ss_am_pm(0x13, "h:mm:ss  AM/PM", "时间格式，将数字格式化为12小时制的时间（带上午/下午标识和秒数）"),

    date_h_mm(0x14, "h:mm", "时间格式，将数字格式化为24小时制的时间（不带日期）"),
    date_h_mm_ss(0x15, "h:mm:ss", "时间格式，将数字格式化为24小时制的时间（带秒数，不带日期）"),
    date_m_d_yy_h_mm(0x16, "m/d/yy  h:mm", "日期时间格式，将数字格式化为月/日/年  小时:分钟形式的日期时间"),

    thousand_contain_negative(0x25, "#,##0_);(#,##0)", "千分位数字格式，将数字格式化为有括号负数格式的整数"),
    thousand_red_contain_negative(0x26, "#,##0_);[Red](#,##0)", "千分位数字格式，将数字格式化为用红色显示的有括号负数格式的整数"),
    thousand_float_contain_negative(0x27, "#,##0.00_);(#,##0.00)", "千分位数字格式，将数字格式化为有括号负数格式的带两位小数点的数字"),
    thousand_float_red_contain_negative(0x28, "#,##0.00_);[Red](#,##0.00)", "千分位数字格式，将数字格式化为用红色显示的有括号负数格式的带两位小数点的数字"),
    thousand_custom(0x29, "_(* #,##0_);_(* (#,##0);_(* \"-\"_);_(@_)", "自定义千分位数字格式，正数显示千分位，负数和零显示-"),

    currency_custom(0x2a, "_(\"$\"* #,##0_);_(\"$\"* (#,##0);_(\"$\"* \"-\"_);_(@_)", "自定义货币格式，正数显示千分位和货币符号$，负数显示括号"),
    thousand_custom_float(0x2b, "_(* #,##0.00_);_(* (#,##0.00);_(* \"-\"??_);_(@_)", "自定义数字格式，正数显示千分位和两位小数点，负数显示-"),
    currency_custom_2(0x2c, "_(\"$\"* #,##0.00_);_(\"$\"* (#,##0.00);_(\"$\"* \"-\"??_);_(@_)", "自定义货币格式，正数显示千分位、货币符号$和两位小数点，负数显示括号"),
    date_mm_ss(0x2d, "mm:ss", "时间格式，将数字格式化为分钟:秒形式"),
    date__h__mm_ss(0x2e, "[h]:mm:ss", "时间格式，将数字格式化为小时:分钟:秒形式，可以超过24小时"),

    date_mm_ss_0(0x2f, "mm:ss.0", "时间格式，将数字格式化为分钟:秒.0形式，带一位小数"),
    scientific_notation(0x30, "##0.0E+0", "科学计数法格式，将数字格式化为不带小数点的科学计数法"),
    text(0x31, "@", "text 文本格式，将数据的显示格式设为文本，不进行任何格式化操作"),

    ;

    private int number;

    private String format;

    private String description;

    public short getShortNumber() {
        return (short) number;
    }

}
