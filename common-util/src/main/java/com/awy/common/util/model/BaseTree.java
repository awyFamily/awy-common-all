package com.awy.common.util.model;

import lombok.Data;

import java.util.List;

/**
 * @author yhw
 * @date 2022-12-10
 */
@Data
public class BaseTree<T extends BaseTree> {

    /**
     * 主键ID
     */
    private Long id;


    /**
     * 名称
     */
    private String name;


    /**
     * 父Id
     */
    private Long parentId;

    /**
     * 排序
     */
    private Integer sorts;

    /**
     * 下级
     */
    private List<T> children;
}
