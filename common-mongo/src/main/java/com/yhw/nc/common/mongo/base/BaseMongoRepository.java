package com.yhw.nc.common.mongo.base;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author yhw
 * @param <T> 实体
 * @param <ID> id类型
 */
public interface BaseMongoRepository<T,ID> extends MongoRepository<T,ID> {

    /**
     * 获取标准分页
     * @param page mongo分页
     * @return 标准分页
     */
    default IPage<T> getPage(Page<T> page){
        if(page == null){
            return new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>();
        }

        Pageable pageable = page.getPageable();
        int pageNumber = 1;
        int pageSize = 10;
        if(pageable != null){
            pageNumber = page.getPageable().getPageNumber();
            pageSize = page.getPageable().getPageSize();
        }

        IPage<T> result = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(pageNumber,pageSize,page.getTotalElements());
        result.setRecords(page.getContent());
        return result;
    }

    default PageRequest getPageRequest(Long page, Long limit, String sortParameter){
        return getPageRequest(page,limit, sortParameter, Sort.Direction.DESC);
    }

    default PageRequest getPageRequest(Long page,Long limit,String sortParameter,Sort.Direction direction){
        page = page - 1;
        return PageRequest.of(page.intValue(),limit.intValue(), direction, sortParameter);
    }

}
