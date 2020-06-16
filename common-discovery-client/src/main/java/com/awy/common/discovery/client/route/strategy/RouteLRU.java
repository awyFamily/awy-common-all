package com.awy.common.discovery.client.route.strategy;

import com.awy.common.discovery.client.route.AbstractRouter;
import org.springframework.cloud.client.ServiceInstance;

import java.util.List;

/**
 * 最久未使用的优先被选举
 * 需要实现 Round(回合) LFC  Failover(故障转移)  ConsistentHash(hash)
 * @author yhw
 */
public class RouteLRU extends AbstractRouter {

    @Override
    public ServiceInstance getServiceInstance(List<ServiceInstance> instances) {
        //Temporarily unrealized
        return instances.get(0);
    }

}
