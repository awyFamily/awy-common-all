package com.yhw.common.email.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yhw
 */
@Getter
@AllArgsConstructor
public enum EmailSendTypeEnum {

    TEXT_SEND(1,"文本模式"),
    TEMPLATE_SEND(2,"模板模式"),
    ;

    private Integer type;

    private String description;

    private static Map<Integer, EmailSendTypeEnum> repository;

    static {
        repository = new HashMap<>();
        for (EmailSendTypeEnum typeEnum : EmailSendTypeEnum.values()) {
            repository.put(typeEnum.getType(),typeEnum);
        }
    }

    public static EmailSendTypeEnum getType(Integer type){
        return repository.get(type);
    }
}
