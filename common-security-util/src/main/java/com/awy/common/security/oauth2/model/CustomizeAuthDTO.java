package com.awy.common.security.oauth2.model;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * custom auth query model
 * @author yhw
 * @date 2022-04-19
 */
@NoArgsConstructor
@Data
public class CustomizeAuthDTO {

    /**
     * 自定义认证方法名称
     */
    private String customizeAuthName;

    public CustomizeAuthDTO(String customizeAuthName) {
        this.customizeAuthName = customizeAuthName;
    }
}
