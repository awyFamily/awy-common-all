package com.awy.common.gateway.handler;

import lombok.AllArgsConstructor;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * reference GatewayControllerEndpoint implements
 * @author yhw
 */
@AllArgsConstructor
@RestController
public class RouteHandler {

    private final ApplicationEventPublisher publisher;

    private final RouteLocator routeLocator;

    private final RouteDefinitionLocator routeDefinitionLocator;

    /**
     * 刷新路由
     * @return
     */
    @PostMapping("/refresh")
    public Mono<Void> refresh() {
        this.publisher.publishEvent(new RefreshRoutesEvent(this));
        return Mono.empty();
    }

    /**
     * 获取所有路由节点
     * @return
     */
    @GetMapping("/routes")
    public Mono<List<Map<String, Object>>> routes() {
        Mono<Map<String, RouteDefinition>> routeDefs = this.routeDefinitionLocator
                .getRouteDefinitions().collectMap(RouteDefinition::getId);
        Mono<List<Route>> routes = this.routeLocator.getRoutes().collectList();
        return Mono.zip(routeDefs, routes).map(tuple -> {
            Map<String, RouteDefinition> defs = tuple.getT1();
            List<Route> routeList = tuple.getT2();
            List<Map<String, Object>> allRoutes = new ArrayList<>();

            routeList.forEach(route -> {
                HashMap<String, Object> r = new HashMap<>();
                r.put("route_id", route.getId());
                r.put("order", route.getOrder());

                if (defs.containsKey(route.getId())) {
                    r.put("route_definition", defs.get(route.getId()));
                }
                else {
                    HashMap<String, Object> obj = new HashMap<>();

                    obj.put("predicate", route.getPredicate().toString());

                    if (!route.getFilters().isEmpty()) {
                        ArrayList<String> filters = new ArrayList<>();
                        for (GatewayFilter filter : route.getFilters()) {
                            filters.add(filter.toString());
                        }

                        obj.put("filters", filters);
                    }

                    if (!obj.isEmpty()) {
                        r.put("route_object", obj);
                    }
                }
                allRoutes.add(r);
            });

            return allRoutes;
        });
    }

}
