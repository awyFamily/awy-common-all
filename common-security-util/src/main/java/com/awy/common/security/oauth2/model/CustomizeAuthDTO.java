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

    private static final String DEFAULT_AUTH_NAME = "用户名密码认证";

    public CustomizeAuthDTO() {
        this.customizeAuthName = DEFAULT_AUTH_NAME;
    }

    public CustomizeAuthDTO(String customizeAuthName) {
        this.customizeAuthName = customizeAuthName;
    }
}
