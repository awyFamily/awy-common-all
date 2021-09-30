package com.awy.common.api;

import com.awy.common.util.constants.CommonConstant;
import com.awy.common.util.model.ApiResult;
import com.awy.common.util.utils.CollUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;

/**
 * 远程API抽象父类
 * Copyright  awyFamily
 * @author yhw
 */
@Slf4j
public abstract class BaseRemoteApi {

    public <T> T getData(ApiResult<T> apiResult){
        if(!this.isInvokeSuccess(apiResult)){
            log.error("remote invoke result : {}" ,apiResult.getMessage());
            return null;
        }
        T data = apiResult.getData();
        if(data instanceof Collection){
            if(CollUtil.isEmpty((Collection<?>) data)){
                return null;
            }
        }
        return apiResult.getData();
    }

    public boolean isInvokeSuccess(ApiResult apiResult){
        return apiResult != null && CommonConstant.RESPONSE_SUCCESS.equals(apiResult.getCode());
    }

    public boolean isInvokeSuccessInArray(ApiResult<? extends Collection> apiResult){
        return this.isInvokeSuccess(apiResult) && CollUtil.isNotEmpty(apiResult.getData());
    }
}
