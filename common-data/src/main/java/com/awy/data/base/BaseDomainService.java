package com.awy.data.base;

import cn.hutool.core.lang.Assert;
import com.awy.data.constants.DataConstant;
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
public abstract class BaseDomainService<M extends IDDDBaseRepository<PO>,F extends BaseFactory<DO,PO>,DO,PO> {

    @Autowired
    private M baseRepository;

    @Autowired
    private F baseFactory;

    public M  getRepository(){
        return this.baseRepository;
    }

    public F getBaseFactory() {
        return baseFactory;
    }

    public DO getInfo(Serializable id) {
        Assert.isFalse(id == null,DataConstant.ID_IS_NULL_ERR_MSG);
        PO po = this.getRepository().getById(id);
        Assert.isFalse(po == null,getByIdIsNullErrMessage());
        return this.getBaseFactory().getInfo(po);
    }

    public DO getSimpleInfo(Serializable id) {
        Assert.isFalse(id == null,DataConstant.ID_IS_NULL_ERR_MSG);
        PO po = this.getRepository().getById(id);
        Assert.isFalse(po == null,getByIdIsNullErrMessage());
        return this.getBaseFactory().getSimpleInfo(po);
    }

    @Transactional(rollbackFor = {Exception.class})
    public DO create(DO model) {
        this.beforeCreate(model);
        PO po = this.getBaseFactory().create(model);
        this.getRepository().insert(po);
        this.afterCreate(model,po);
        return model;
    }

    protected abstract  void beforeCreate(DO model);

    protected abstract  void afterCreate(DO model,PO po);

    @Transactional(rollbackFor = {Exception.class})
    public DO modify(DO model) {
        PO po = this.beforeModify(model);
        po = this.getBaseFactory().edit(model,po);
        this.getRepository().updateById(po);
        this.afterModify(model,po);
        return model;
    }

    protected abstract  PO beforeModify(DO model);

    protected abstract  void afterModify(DO model,PO po);

    @Transactional(rollbackFor = {Exception.class})
    public boolean deleteById(Serializable id){
        Assert.isFalse(id == null,DataConstant.ID_IS_NULL_ERR_MSG);
        PO po = this.getRepository().getById(id);
        Assert.isFalse(po == null, getByIdIsNullErrMessage());
        this.beforeDelete(po);
        this.getRepository().deleteById(id);
        this.afterDelete(po);
        return true;
    }

    protected abstract  void beforeDelete(PO po);

    protected abstract  void afterDelete(PO po);

    /**
     * 获取根据ID查询为null错误提示
     * @return ID查询为null错误提示
     */
    public String getByIdIsNullErrMessage() {
        return DataConstant.GET_ID_IS_NULL_ERR_MSG;
    }

}
