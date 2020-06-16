package com.awy.common.log.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yhw
 */
@AllArgsConstructor
@Getter
public enum LogTypeEnum {

    STANDARD(0,"标准日志"),
    READ_ONLY(1,"只读日志"),

    WRITE(2,"写操作日志"),
    LOGIN(3,"登录日志"),
    ;

    /**
     * 类型
     */
    private Integer code;

    /**
     * 描述
     */
    private String description;


    private static Map<Integer,LogTypeEnum> repository;

    static {
        repository = new HashMap<>();
        for (LogTypeEnum typeEnum : LogTypeEnum.values()) {
            repository.put(typeEnum.code,typeEnum);
        }
    }

    public static LogTypeEnum getLogTypeEnum(int code){
        return repository.get(code);
    }
}
