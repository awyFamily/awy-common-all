package com.yhw.nc.common.discovery.client.route.strategy;

import com.yhw.nc.common.discovery.client.route.AbstractRouter;
import org.springframework.cloud.client.ServiceInstance;

import java.util.List;

/**
 * @author yhw
 */
public class RouteLast extends AbstractRouter {
    @Override
    public ServiceInstance getServiceInstance(List<ServiceInstance> instances) {
        return instances.get(instances.size()-1);
    }
}
