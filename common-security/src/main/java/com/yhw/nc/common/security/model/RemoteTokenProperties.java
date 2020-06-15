package com.yhw.nc.common.security.model;

import lombok.Data;

import java.util.List;

/**
 * 远程 token 配置
 * 通过配置文件读取
 * Created by YHW on 2019/8/2.
 */
@Data
public class RemoteTokenProperties {
    private String client_id;
    private String client_secret;
    private String check_token_uri;
    //放行url(不需要认证的uri)
    private String[] ignore_uri;
}
