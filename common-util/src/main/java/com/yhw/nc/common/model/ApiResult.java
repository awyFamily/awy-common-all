package com.yhw.nc.common.model;

import com.yhw.nc.common.constants.CommonConstant;
import lombok.Data;

import java.io.Serializable;

/**
 * 通用服务响应对象
 * @param <T>
 * @author yhw
 */
@Data
public class ApiResult<T> implements Serializable {

    private int code = CommonConstant.RESPONSE_SUCCESS;

    private boolean isSuccess = true;

    private String message = "";

    private T data;

    private ApiResult() {
    }

    private ApiResult(int code, boolean seccess, String message, T data) {
        this.code = code;
        this.message = message;
        this.isSuccess = seccess;
        this.data = data;
    }

    private ApiResult code(int code) {
        this.code = code;
        return this;
    }

    private ApiResult isSuceess(Boolean isSuceess) {
        this.isSuccess = isSuceess;
        return this;
    }

    private ApiResult message(String message) {
        this.message = message;
        return this;
    }

    private ApiResult data(T date) {
        this.data = date;
        return this;
    }

    public static <T> ApiResult ok() {
        return new ApiResult().data(null);
    }

    public static <T> ApiResult ok(T t) {
        return new ApiResult().data(t);
    }

    public static <T> ApiResult ok(T t, int code) {
        return ok(t,code,"");
    }

    public static <T> ApiResult ok(T t, String message) {
        return ok(t,CommonConstant.RESPONSE_SUCCESS,message);
    }

    public static <T> ApiResult ok(T t, int code, String message) {
        return new ApiResult().data(t).code(code).message(message);
    }

    public static <T> ApiResult error() {
        return error("");
    }

    public static <T> ApiResult error(Exception e) {
        return error(e.getMessage());
    }

    public static <T> ApiResult error(Throwable throwable) {
        return error(throwable.getMessage());
    }

    public static <T> ApiResult error(String message) {
        return new ApiResult().isSuceess(false).code(CommonConstant.RESPONSE_ERROR).message(message);
    }

    public static  ApiResult timeOut() {
        return timeOut("");
    }

    public static <T>  ApiResult timeOut(Exception e) {
        return timeOut(e.getMessage());
    }

    public static <T>  ApiResult timeOut(Throwable throwable) {
        return timeOut(throwable.getMessage());
    }

    public static <T>  ApiResult timeOut(String message) {
        return new ApiResult().isSuceess(false).code(CommonConstant.RESPONSE_TIMEOUT).message(message);
    }

    public static Builder getBuilder(){
        return new Builder();
    }

    public static class Builder<T>{
        private int code;

        private boolean isSuccess;

        private String message;

        private T data;

        private Builder(){}

        public Builder setCode(int code){
            this.code = code;
            return this;
        }

        public Builder setMessage(String message){
            this.message = message;
            return this;
        }

        public Builder setSuccess(boolean success){
            this.isSuccess = success;
            return this;
        }

        public Builder setData(T data){
            this.data = data;
            return this;
        }

        public ApiResult builder(){
            return new ApiResult(this.code, this.isSuccess, this.message, this.data);
        }
    }

}
