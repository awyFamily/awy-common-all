package com.awy.data.config;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.awy.common.security.oauth2.model.AuthUser;
import com.awy.common.util.utils.SecurityUtil;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 字段自动填充
 * Created by YHW on 2019/8/13.
 */
@Component
public class MyMetaObjectHandler implements MetaObjectHandler{

    @Override
    public void insertFill(MetaObject metaObject) {
        AuthUser authUser = SecurityUtil.getUser();
        if(ObjectUtil.isNotEmpty(authUser)){
            metaObject.setValue("createBy", authUser.getUserId());
            metaObject.setValue("updateBy", authUser.getUserId());
        }

        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime", LocalDateTime.now());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        AuthUser authUser = SecurityUtil.getUser();
        if(ObjectUtil.isNotEmpty(authUser)){
            metaObject.setValue("updateBy", authUser.getUserId());
        }
        metaObject.setValue("updateTime", LocalDateTime.now());
    }
}
