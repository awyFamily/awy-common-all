package com.yhw.nc.common.excel;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
public class UserVO {

    private String name;
    private Integer age;
    private Date createTime;
}
