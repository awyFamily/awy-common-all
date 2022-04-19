package com.awy.common.security.oauth2.model;

import lombok.Data;

/**
 * custom auth query model
 * @author yhw
 * @date 2022-04-19
 */
@Data
public class CustomizeAuthDTO {

    /**
     * 自定义认证方法名称
     */
    private final String customizeAuthName;

    public CustomizeAuthDTO(String customizeAuthName) {
        this.customizeAuthName = customizeAuthName;
    }
}
