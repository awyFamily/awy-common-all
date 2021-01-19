package com.awy.common.api;

import com.awy.common.util.constants.CommonConstant;
import com.awy.common.util.model.ApiResult;
import com.awy.common.util.utils.CollUtil;

import java.util.Collection;

/**
 * 远程API抽象父类
 * @author yhw
 */
public abstract class BaseRemoteApi {

    public boolean isInvokeSuccess(ApiResult apiResult){
        return apiResult != null && CommonConstant.RESPONSE_SUCCESS.equals(apiResult.getCode());
    }

    public boolean isInvokeSuccessInArray(ApiResult<? extends Collection> apiResult){
        return this.isInvokeSuccess(apiResult) && CollUtil.isEmpty(apiResult.getData());
    }
}
