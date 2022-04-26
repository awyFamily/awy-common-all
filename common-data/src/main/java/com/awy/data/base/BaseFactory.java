package com.awy.data.base;

import cn.hutool.core.bean.BeanUtil;
import com.awy.common.util.utils.ReflexUtils;

/**
 * @author yhw
 * @date 2022-04-26
 */
public abstract class BaseFactory<DO,PO> {

    public abstract PO create(DO domainEntity);

    public abstract PO edit(DO domainEntity);

    public DO getSimpleInfo(PO po) {
        DO domainEntity = (DO)ReflexUtils.getClassAllField(po.getClass());
        BeanUtil.copyProperties(po,domainEntity);
        return domainEntity;
    }

    public DO getInfo(PO po) {
        DO domainEntity =  this.getSimpleInfo(po);
        this.wrapperInfo(domainEntity);
        return domainEntity;
    }

    public abstract void wrapperInfo(DO domainEntity);

}
