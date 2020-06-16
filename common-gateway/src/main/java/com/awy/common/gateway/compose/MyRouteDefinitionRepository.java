//package com.yhw.nc.common.gateway.compose;
//
//import com.google.common.collect.Lists;
//import com.google.common.collect.Maps;
//import lombok.AllArgsConstructor;
//import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
//import org.springframework.cloud.gateway.route.RouteDefinition;
//import org.springframework.cloud.gateway.route.RouteDefinitionRepository;
//import org.springframework.stereotype.Component;
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;
//
//import java.net.URI;
//import java.util.List;
//import java.util.Map;
//
///**
// * 此处配置，针对特定的示例进行负载均衡
// * @author yhw
// */
//@AllArgsConstructor
//@Component
//public class MyRouteDefinitionRepository implements RouteDefinitionRepository {
//
//
//    //1.通过成员变量获取   设置一个定时器，定时获取最新网关数据（减少频繁IO请求）
//    //2.直接读取相应配置获取
//
//
//    @Override
//    public Flux<RouteDefinition> getRouteDefinitions(){
//        //加载(如果加装的不是serverID 可以根据其他协议规则进行负载均衡)
//        RouteDefinition definition = new RouteDefinition();
//        definition.setId("baidu");
//        try{
//            //路径
////            definition.setUri(new URI("https://www.baidu.com"));
//            definition.setUri(new URI("lb://nacos-test"));
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//
//        List<PredicateDefinition> list = Lists.newArrayList();
//        PredicateDefinition predicateDefinition = new PredicateDefinition();
//        Map<String,String> args = Maps.newHashMap();
//        args.put("_genkey_0","/baidu/**");
//
//        predicateDefinition.setArgs(args);
//        predicateDefinition.setName("Path");
//        list.add(predicateDefinition);
//
//        definition.setPredicates(list);
//
//        System.out.println("auto poll current method");
//        return Flux.just(definition);
////        return Flux.empty();
//    }
//
//    @Override
//    public Mono<Void> save(Mono<RouteDefinition> route) {
//        System.out.println("实现为空即可");
//        return null;
//    }
//
//    @Override
//    public Mono<Void> delete(Mono<String> routeId) {
//        System.out.println("实现为空即可");
//        return null;
//    }
//}
