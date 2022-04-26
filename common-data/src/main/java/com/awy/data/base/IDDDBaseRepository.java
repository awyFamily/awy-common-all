package com.awy.data.base;

import java.io.Serializable;
import java.util.Collection;

public interface IDDDBaseRepository<T> {

    int insert(T t);

    int insertBatch(Collection<T> list);

    int updateById(T t);

    int deleteById(Serializable id);

    int deleteByIds(Collection<Serializable> ids);

    T getById(Serializable id);


}
