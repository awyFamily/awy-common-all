package com.awy.data.base;

/**
 * @author yhw
 * @date 2022-04-26
 */
public abstract class BaseFactory<DO,PO> {

    public abstract PO create(DO domainEntity);

    public abstract PO edit(DO domainEntity,PO po);

    private DO model;

    public abstract DO getSimpleInfo(PO po);

    public DO getInfo(PO po) {
        DO domainEntity =  this.getSimpleInfo(po);
        this.wrapperInfo(domainEntity);
        return domainEntity;
    }

    public abstract void wrapperInfo(DO domainEntity);

}
