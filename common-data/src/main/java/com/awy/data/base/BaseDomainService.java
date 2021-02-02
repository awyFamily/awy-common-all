package com.awy.data.base;

import cn.hutool.core.lang.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;

/**
 * 领域层基类
 * @author YHW
 * @param <M> repository
 * @param <DO> 领域层对象
 * @param <PO> 数据库映射对象
 */
public abstract class BaseDomainService<M extends IDDDBaseRepository<PO>,DO,PO> {


    @Autowired
    private M baseRepository;

    public M  getRepository(){
        return this.baseRepository;
    }


    public abstract DO getInfo();

    @Transactional(rollbackFor = {Exception.class})
    public DO create(DO model) {
        PO po = this.beforeCreate(model);
        this.getRepository().insert(po);
        this.afterCreate(model,po);
        return model;
    }

    protected abstract  PO beforeCreate(DO model);

    protected abstract  PO afterCreate(DO model,PO po);

    @Transactional(rollbackFor = {Exception.class})
    public DO modify(DO model) {
        PO po = this.beforeModify(model);
        this.getRepository().updateById(po);
        this.afterModify(model,po);
        return model;
    }

    protected abstract  PO beforeModify(DO model);

    protected abstract  PO afterModify(DO model,PO po);

    @Transactional(rollbackFor = {Exception.class})
    public boolean deleteById(Serializable id){
        Assert.isFalse(id == null,"id is null");
        PO po = this.getRepository().getById(id);
        Assert.isFalse(po == null,"delete fail . po can't be found by id");
        this.beforeDelete(po);
        this.getRepository().deleteById(id);
        this.afterDelete(po);
        return true;
    }

    protected abstract  void beforeDelete(PO po);

    protected abstract  void afterDelete(PO po);


}
