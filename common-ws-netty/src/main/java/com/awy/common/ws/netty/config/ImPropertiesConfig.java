package com.awy.common.ws.netty.config;

import com.awy.common.ws.netty.constants.ImCommonConstant;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


/**
 * @author yhw
 */
@ConfigurationProperties(prefix = ImCommonConstant.IM_NT_PREFIX)
@Component
@Data
public class ImPropertiesConfig {

    /**
     * webSocket前缀
     */
    private String websocketPath;

    /**
     * 扫描消息包 路径
     */
    private String packetScan;

    /**
     * websocket 端口
     */
    private Integer port;

    /**
     * 是否启用ssl
     */
    private boolean ssl = false;

    /**
     * 是否集群模式
     */
    private boolean cluster = false;

    /**
     * 是否注册 到 注册中心(和 spring web 容器 同时注册还为实现)
     * 当前版本 建议不使用  spring web
     * 由Spring cloud 提供
     */
    private boolean register = false;


}
