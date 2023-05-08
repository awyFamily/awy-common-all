package com.awy.data.base;

import cn.hutool.core.util.StrUtil;
import com.awy.common.util.model.BasePageDTO;
import com.awy.common.util.utils.CollUtil;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.Collection;


/**
 * @author yhw
 * @date 2022-04-26
 */
public class BaseRepositoryImpl<M extends BaseMapper<T>, T> implements BaseRepository<T> {

    @Getter
    @Setter
    @Autowired
    private M baseMapper;

    @Override
    public int insert(T t) {
        return baseMapper.insert(t);
    }

    @Override
    public int insertBatch(Collection<T> list) {
        if (CollUtil.isEmpty(list)) {
            return 0;
        }
        for (T t : list) {
            this.insert(t);
        }
        return list.size();
    }

    @Override
    public int updateById(T t) {
        return baseMapper.updateById(t);
    }

    @Override
    public T getById(Serializable id) {
        return baseMapper.selectById(id);
    }

    @Override
    public int deleteById(Serializable id) {
        return baseMapper.deleteById(id);
    }

    @Override
    public int deleteByIds(Collection<? extends Serializable> ids) {
        if (CollUtil.isEmpty(ids)) {
            return 0;
        }
        return this.getBaseMapper().deleteBatchIds(ids);
    }

    public IPage getQueryPage(BasePageDTO dto) {
        Page queryPage = new Page<>(dto.getCurrent(), dto.getSize());
        if (CollUtil.isNotEmpty(dto.getColumns())) {
            OrderItem[] items = new OrderItem[dto.getColumns().size()];
            for (int i = 0; i < dto.getColumns().size(); i++) {
                if (dto.isAsc()) {
                    items[i] = OrderItem.asc(StrUtil.toUnderlineCase(dto.getColumns().get(i)));
                } else {
                    items[i] = OrderItem.desc(StrUtil.toUnderlineCase(dto.getColumns().get(i)));
                }
            }
            queryPage.addOrder(items);
        }
        return queryPage;
    }


}
