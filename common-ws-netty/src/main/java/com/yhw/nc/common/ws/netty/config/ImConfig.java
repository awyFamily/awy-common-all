package com.yhw.nc.common.ws.netty.config;

import lombok.Getter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * @author yhw
 */
@Component
public class ImConfig implements InitializingBean {

    private static ImConfig imConfig = null;

    public static ImConfig getImConfig() {
        return imConfig;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        imConfig = this;
    }

    @Getter
    @Autowired
    private ImPropertiesConfig propertiesConfig;

    @Getter
    @Autowired
    private ApplicationContext applicationContext;

}
