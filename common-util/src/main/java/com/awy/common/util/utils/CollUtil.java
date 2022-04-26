package com.awy.common.util.utils;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.comparator.ComparatorChain;
import cn.hutool.core.util.ObjectUtil;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author yhw
 */
public class CollUtil extends cn.hutool.core.collection.CollUtil {

    private CollUtil(){}

    public static <T,K> Map<K,T>  conversionHashMap(Collection<T> collection, Function<? super T, ? extends K> groupKey){
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


    public  static <T,K> Map<K,T>  conversionConcurrentMap(Collection<T> collection, Function<? super T, ? extends K> groupKey){
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

    /**
     * 多字段排序
     * @param source 待排序数组
     * @param reverse  否反序，true表示正序，false反序
     * @param sortKeys 排序数组
     * @param <T> 排序对象
     * @param <K> 排序Key
     * @return 排序后列表
     */
    public static <T,K> void   sorts(List<T> source,boolean reverse,Function<? super T, ? extends K>... sortKeys) {
        if (isEmpty(source) || sortKeys == null || sortKeys.length == 0) {
            return;
        }
        ComparatorChain<T> chain = new ComparatorChain<>();
        Function keyExtractor;
        for (Function<? super T, ? extends K> sortKey : sortKeys) {
            keyExtractor = sortKey;
            chain.addComparator(Comparator.comparing(keyExtractor),reverse);
        }
        CollectionUtil.sort(source,chain);
    }

}
