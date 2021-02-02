package com.awy.data.base;

import java.io.Serializable;
import java.util.Collection;

public interface IDDDBaseRepository<T> {

    boolean insert(T t);

    boolean insertBatch(Collection<T> list);

    boolean updateById(T t);

    boolean deleteById(Serializable id);

    boolean deleteByIds(Collection<Serializable> ids);

    T getById(Serializable id);


}
