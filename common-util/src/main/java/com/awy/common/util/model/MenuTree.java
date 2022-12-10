package com.awy.common.util.model;

import lombok.Data;

import java.io.Serializable;

/**
 * 菜单树
 * @author yhw
 */
@Data
public class MenuTree extends BaseTree implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 菜单路径
     */
    private String uri;

    /**
     * 几级菜单(层级)
     */
    private Integer level;

    /**
     * 图标路径
     */
    private String icon;

    /**
     * 备注
     */
    private String remarks;

}
