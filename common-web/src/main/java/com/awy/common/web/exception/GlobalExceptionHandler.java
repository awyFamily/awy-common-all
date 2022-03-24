package com.awy.common.web.exception;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.awy.common.util.constants.CommonConstant;
import com.awy.common.util.model.ApiResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 全局异常拦截
 * @author yhw
 */
@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {

    /**
     * Valid 全局异常
     * @param exception 异常详细信息
     * @return ApiResult
     */
    @ResponseStatus(HttpStatus.PRECONDITION_REQUIRED)
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ApiResult methodArgumentNotValidHandler(MethodArgumentNotValidException exception){
        return this.validHandler(exception.getBindingResult());
    }

    @ResponseStatus(HttpStatus.PRECONDITION_REQUIRED)
    @ExceptionHandler(value = BindException.class)
    public ApiResult bindExceptionHandler(BindException exception){
        return this.validHandler(exception.getBindingResult());
    }

    private ApiResult validHandler(BindingResult bindingResult){
        //按需重新封装需要返回的错误信息
        JSONArray jsonArray = new JSONArray();
        JSONObject errorJson;
        if(bindingResult != null){
            //解析原错误信息，封装后返回，此处返回非法的字段名称，原始值，错误信息
            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                errorJson = new JSONObject();
                errorJson.put("defaultMessage",fieldError.getDefaultMessage());
                errorJson.put("field",fieldError.getField());
                errorJson.put("rejectedValue",fieldError.getRejectedValue());
                jsonArray.add(errorJson);
            }
        }
        return ApiResult.getBuilder()
                .setMessage(jsonArray.toString())
                .setCode(CommonConstant.RESPONSE_ERROR)
                .setSuccess(false)
                .builder();
    }

    /**
     * 对象转换出错异常
     * @return ApiResult
     */
    @ResponseStatus(HttpStatus.PRECONDITION_REQUIRED)
    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    public ApiResult httpMessageNotReadableExceptionHandler(){
        return ApiResult.getBuilder()
                .setMessage("对象转换错误，请仔细检查对象参数是否匹配")
                .setCode(CommonConstant.RESPONSE_ERROR)
                .setSuccess(false)
                .builder();
    }


    @ResponseStatus(HttpStatus.PRECONDITION_REQUIRED)
    @ExceptionHandler(value = MissingServletRequestParameterException.class)
    public ApiResult missingServletRequestParameterExceptionHandler(MissingServletRequestParameterException exception){
        return ApiResult.getBuilder()
                .setMessage("必填参数：[".concat(exception.getParameterName()).concat("]为空"))
                .setCode(CommonConstant.RESPONSE_ERROR)
                .setSuccess(false)
                .builder();
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = IllegalArgumentException.class)
    public ApiResult illegalArgumentException(IllegalArgumentException exception){
        return ApiResult.getBuilder()
                .setMessage(exception.getMessage())
                .setCode(CommonConstant.RESPONSE_ERROR)
                .setSuccess(false)
                .builder();
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = NullPointerException.class)
    public ApiResult nullPointerException(NullPointerException exception){
        return ApiResult.getBuilder()
                .setMessage("空指针异常,请联系后台管理员")
                .setCode(CommonConstant.RESPONSE_ERROR)
                .setSuccess(false)
                .builder();
    }

    //可以定义自己框架的异常，进行统一异常处理

}