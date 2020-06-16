package com.awy.common.email.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yhw
 */
@Getter
@AllArgsConstructor
public enum EmailFtlEnum {

    TEST(1,"测试","测试"),
    ;

    private Integer code;

    private String name;

    private String filePath;

    private static Map<Integer, EmailFtlEnum> repository;

    static {
        repository = new HashMap<>();
        for (EmailFtlEnum ftlEnum : EmailFtlEnum.values()) {
            repository.put(ftlEnum.getCode(),ftlEnum);
        }
    }

    public static EmailFtlEnum getCode(Integer code){
        return repository.get(code);
    }
}
