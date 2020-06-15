package com.yhw.nc.common.gateway.config;

import com.yhw.nc.common.discovery.client.route.RouterEum;
import lombok.Data;

import java.util.List;

/**
 * @author yhw
 */
@Data
public class AuthFilterProperties {
    /**
     * 认证服务器  注册中心服务ID
     */
    private String serviceId;
    /**
     * 校验token 地址后缀
     */
    private String checkUriSuffix = "/oauth/check_token";

    /**
     * 路由规则，默认0
     * {@link RouterEum#getCode()}
     */
    private Integer routerCode = 0;

    /**
     * 是否放行所有
     */
    private Boolean ignoreAll = false;


    /**
     * 放行的路径地址
     */
    private List<String> releaseUrl;
}
