package com.yhw.nc.common.discovery.client.route;

import org.springframework.cloud.client.ServiceInstance;

import java.util.List;

/**
 * @author yhw
 */
public abstract class AbstractRouter {

    public abstract ServiceInstance getServiceInstance(List<ServiceInstance> instances);
}
