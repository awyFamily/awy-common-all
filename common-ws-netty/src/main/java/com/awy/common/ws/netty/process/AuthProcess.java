package com.awy.common.ws.netty.process;

import com.awy.common.ws.netty.model.ImSession;

/**
 * web认证
 * @author yhw
 */
public abstract class AuthProcess {

    /**
     * 登录事件
     * @param userName 用户名
     * @param password 用户密码
     * @return IM用户对象
     */
    public abstract ImSession login(String userName, String password);


}
