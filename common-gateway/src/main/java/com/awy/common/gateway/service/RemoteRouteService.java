package com.awy.common.gateway.service;

import cn.hutool.json.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.awy.common.discovery.client.util.RestTemplateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.support.NameUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 动态获取路由表
 * remote route service
 * @author yhw
 */
@Slf4j
@Service
public class RemoteRouteService implements InitializingBean, DisposableBean {

    @Value("${spring.cloud.gateway.remote-uri}")
    private String remoteUri;

    @Resource
    private DynamicRouteService dynamicRouteService;

    @Resource
    private RestTemplate restTemplate;

    private volatile boolean isStop = false;

    private Thread dynamicRouteThread;


    @Override
    public void afterPropertiesSet() throws Exception {
        initRouteDefinitions();
    }


    private void initRouteDefinitions(){
        dynamicRouteThread = new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject result;
                List<Map> maps;
                RouteDefinition routeDefinition;
                while (!isStop){
                    try {
                        result = RestTemplateUtil.getForObj(restTemplate, remoteUri, JSONObject.class);
                        if(result != null && result.getInt("code") == 200){
                            maps = result.getJSONArray("data").toList(Map.class);
                            if(maps != null){
                                for (Map map : maps) {
                                    routeDefinition = new RouteDefinition();
                                    routeDefinition.setId(map.get("instanceName").toString());
                                    routeDefinition.setUri(new URI(map.get("instanceUri").toString()));
                                    routeDefinition.setPredicates(getPredicates(map.get("routeUri").toString()));
                                    routeDefinition.setOrder(Integer.valueOf(map.get("sorts").toString()));
                                    dynamicRouteService.update(routeDefinition);
                                }
                            }
                        }
                        TimeUnit.SECONDS.sleep(60);
                    } catch (InterruptedException e) {
                        log.error("get remote route error",e);
                        e.printStackTrace();
                    }catch (Exception e){
                        log.error("get remote route error",e);
                    }
                }
            }
        });
        dynamicRouteThread.setDaemon(true);
        dynamicRouteThread.setName("dynamic-route-thread");
        dynamicRouteThread.start();
    }


    private List<PredicateDefinition> getPredicates(String routeUri){
        List<PredicateDefinition> list = Lists.newArrayList();

        PredicateDefinition pathPredicate = new PredicateDefinition();
        Map<String,String> args = Maps.newHashMap();
        args.put(NameUtils.generateName(0),routeUri);

        pathPredicate.setArgs(args);
        pathPredicate.setName("Path");

        list.add(pathPredicate);
        return list;
    }




    @Override
    public void destroy() throws Exception {
        isStop = true;

        if (dynamicRouteThread.getState() != Thread.State.TERMINATED){
            dynamicRouteThread.interrupt();
            dynamicRouteThread.join();
        }

    }
}
