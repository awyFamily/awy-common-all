package com.awy.common.util.model;

import lombok.Data;

import java.util.List;

/**
 * 分页查询通用DTO
 * @author yhw
 */
@Data
public class BasePageDTO {

    /**
     * 每页显示条数，默认 10
     */
    private long size = 10;

    /**
     * 当前页
     */
    private long current = 1;

    /**
     * 需要进行排序的字段
     */
    private List<String> columns;

    /**
     * 是否正序排列，默认 true
     */
    private boolean asc = true;


}
