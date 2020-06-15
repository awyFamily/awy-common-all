package com.yhw.nc.common.discovery.client.route;

import com.yhw.nc.common.discovery.client.route.strategy.RouteFirst;
import com.yhw.nc.common.discovery.client.route.strategy.RouteLRU;
import com.yhw.nc.common.discovery.client.route.strategy.RouteLast;
import com.yhw.nc.common.discovery.client.route.strategy.RouteRandom;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yhw
 */
@Getter
@AllArgsConstructor
public enum RouterEum {
    ROUTE_RANDOM(0,new RouteRandom()),
    ROUTE_FIRST(1,new RouteFirst()),
    ROUTE_LAST(2,new RouteLast()),
    ROUTE_LRU(3,new RouteLRU()),
    ;

    /**
     * 编码
     */
    private Integer code;

    private AbstractRouter router;

    private static Map<Integer,RouterEum> repositry;

    static {
        repositry = new HashMap<>();
        for (RouterEum eum : RouterEum.values()) {
            repositry.put(eum.getCode(),eum);
        }
    }

    public static RouterEum getType(Integer code){
        RouterEum eum = repositry.get(code);
        if(eum == null){
            eum = RouterEum.ROUTE_RANDOM;
        }
        return eum;
    }
}
