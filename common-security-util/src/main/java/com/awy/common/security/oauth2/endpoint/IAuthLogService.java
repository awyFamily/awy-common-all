package com.awy.common.security.oauth2.endpoint;


import com.awy.common.security.oauth2.model.AuthUser;

/**
 * @author yhw
 * @date 2022-04-19
 */
public interface IAuthLogService {

    void insertLoginLog(AuthUser authUser, String methodName, String remarks);
}
