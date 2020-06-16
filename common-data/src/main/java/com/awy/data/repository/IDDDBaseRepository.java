package com.awy.data.repository;

import java.util.Collection;

public interface IDDDBaseRepository<T,ID> {

    ID insert(T t);

    boolean insertBatch(Collection<T> list);

    boolean updateById(T t);

    boolean deleteById(ID id);

    boolean deleteByIds(Collection<ID> ids);

    T getById(ID id);


}
