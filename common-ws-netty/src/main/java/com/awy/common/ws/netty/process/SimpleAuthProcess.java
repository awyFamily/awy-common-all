package com.awy.common.ws.netty.process;

import com.awy.common.message.api.util.IdUtil;
import com.awy.common.ws.netty.model.ImSession;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yhw
 */
@Slf4j
public class SimpleAuthProcess extends AuthProcess {

    @Override
    public ImSession login(String userName, String password) {
        String userId = IdUtil.randomId();
        log.info("user login success , username is {} ",userName);
        return new ImSession(userId,userName,(byte)1);
    }
}
