package com.yhw.nc.common.discovery.client.route.strategy;

import com.yhw.nc.common.discovery.client.route.AbstractRouter;
import org.springframework.cloud.client.ServiceInstance;

import java.util.List;
import java.util.Random;

/**
 * @author yhw
 */
public class RouteRandom extends AbstractRouter {

    private static Random localRandom = new Random();

    @Override
    public ServiceInstance getServiceInstance(List<ServiceInstance> instances) {
        return instances.get(localRandom.nextInt(instances.size()));
    }
}
