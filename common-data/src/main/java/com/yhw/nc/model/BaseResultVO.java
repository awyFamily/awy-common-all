package com.yhw.nc.model;

import cn.hutool.core.util.ObjectUtil;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@NoArgsConstructor
@Data
public class BaseResultVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 修改人
     */
    private String updateBy;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    private LocalDateTime updateTime;

    /**
     * 备注
     */
    private String remarks;

    public BaseResultVO(String remarks){
        this.remarks = remarks;
    }

    public BaseResultVO(String remarks,String createBy,String updateBy){
        this.remarks = remarks;
        this.createBy = createBy;
        this.updateBy = updateBy;
    }

    public BaseResultVO(String remarks,LocalDateTime createTime, LocalDateTime updateTime){
        this.remarks = remarks;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    public BaseResultVO(String remarks,String createBy,String updateBy,LocalDateTime createTime, LocalDateTime updateTime){
        this.remarks = remarks;
        this.createBy = createBy;
        this.updateBy = updateBy;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }




    /**
     * 获取创建时间戳
     * @return 时间戳
     */
    public Long getCreateTimestamp(){
        if(ObjectUtil.isEmpty(this.createTime)){
            return null;
        }
        return this.createTime.toInstant(ZoneOffset.of("+8")).toEpochMilli();
    }

    /**
     * 获取修改时间戳
     * @return 时间戳
     */
    public Long getUpdateTimestamp(){
        if(ObjectUtil.isEmpty(this.updateTime)){
            return null;
        }
        return this.updateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli();
    }

}
