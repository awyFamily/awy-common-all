package com.awy.common.excel;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class User {

    private String name;
    private Integer age;
    private LocalDateTime createTime;
}
