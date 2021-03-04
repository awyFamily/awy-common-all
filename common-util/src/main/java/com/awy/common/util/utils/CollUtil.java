package com.awy.common.util.utils;

import cn.hutool.core.util.ObjectUtil;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author yhw
 */
public final class CollUtil extends cn.hutool.core.collection.CollUtil {

    private CollUtil(){}

    public final static <T,K> Map<K,T>  conversionHashMap(Collection<T> collection, Function<? super T, ? extends K> groupKey){
        if(CollUtil.isEmpty(collection) || groupKey == null){
            return new HashMap();
        }
        Map<? extends K, List<T>> map = collection.stream().filter(obj -> ObjectUtil.isNotNull(obj))
                .collect(Collectors.groupingBy(groupKey));
        if(CollUtil.isEmpty(map)){
            return new HashMap();
        }
        Map<K,T> resultMap = new HashMap();
        for (Map.Entry<? extends K, List<T>> entry : map.entrySet()) {
            resultMap.put(entry.getKey(),entry.getValue().get(0));
        }
        return resultMap;
    }


    public final static <T,K> Map<K,T>  conversionConcurrentMap(Collection<T> collection, Function<? super T, ? extends K> groupKey){
        if(CollUtil.isEmpty(collection) || groupKey == null){
            return new ConcurrentHashMap<>();
        }
        Map<? extends K, List<T>> map = collection.stream().filter(obj -> ObjectUtil.isNotNull(obj))
                .collect(Collectors.groupingByConcurrent(groupKey));
        if(CollUtil.isEmpty(map)){
            return new ConcurrentHashMap<>();
        }
        Map<K,T> resultMap = new ConcurrentHashMap<>();
        for (Map.Entry<? extends K, List<T>> entry : map.entrySet()) {
            resultMap.put(entry.getKey(),entry.getValue().get(0));
        }
        return resultMap;
    }
}
