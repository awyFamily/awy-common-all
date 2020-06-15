package com.yhw.nc.common.web.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "nccloud.config.web")
@Data
public class WebConfigProperties {

    private List<String>  ignoreJsonResponseUrls;

    private Boolean ignoreAll = false;
}
