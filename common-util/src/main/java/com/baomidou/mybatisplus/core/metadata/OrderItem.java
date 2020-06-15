package com.baomidou.mybatisplus.core.metadata;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * 排序元素载体
 *
 * @author HCL
 * Create at 2019/5/27
 */
@Data
@Accessors(chain = true)
@ToString
public class OrderItem {
    /**
     * 需要进行排序的字段
     */
    private String column;

    /**
     * 是否正序排列，默认 true
     */
    private boolean asc = true;

    public static OrderItem asc(String column) {
        return build(column, true);
    }

    public static OrderItem desc(String column) {
        return build(column, false);
    }

    private static OrderItem build(String column, boolean asc) {
        OrderItem item = new OrderItem();
        item.setColumn(column);
        item.setAsc(asc);
        return item;
    }
}
