package com.yhw.nc.common.discovery.client.util;

import com.yhw.nc.common.discovery.client.route.RouterEum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;

import java.util.List;

/**
 * 服务实例获取器
 * @author yhw
 */
public class ServiceInstanceUtil {

    private static Logger logger = LoggerFactory.getLogger(ServiceInstanceUtil.class);

    /**
     * 轮询选举
     * @param discoveryClient 客户端
     * @param serviceId 服务id
     * @return 服务实例
     */
    public static ServiceInstance getServiceInstance(DiscoveryClient discoveryClient, String serviceId){
        return getServiceInstance(discoveryClient,serviceId,RouterEum.ROUTE_RANDOM);
    }

    /**
     * 根据路由类型 选择类型
     * @param discoveryClient 客户端
     * @param serviceId 服务id
     * @param routerCode 路由类型
     * @return 服务实例
     */
    public static ServiceInstance getServiceInstance(DiscoveryClient discoveryClient, String serviceId, int routerCode){
        return getServiceInstance(discoveryClient,serviceId,RouterEum.getType(routerCode));
    }

    public static ServiceInstance getServiceInstance(DiscoveryClient discoveryClient, String serviceId, RouterEum routerEum){
        if(serviceId == null || serviceId.isEmpty()){
            logger.error("\n >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> \n" +
                    ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> \n" +
                    ">>>>>>>>>>>>>>>>>>>>>>>>>>>>> 【 serviceId isEmpty 】\n" +
                    ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> \n" +
                    ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
            return null;
        }
        if(discoveryClient == null){
            logger.error("\n>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> \n" +
                    ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> \n" +
                    ">>>>>>>>>>>>>>>>>>>>>>>>>>>>> 【 discoveryClient isEmpty 】\n" +
                    ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> \n" +
                    ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
            return null;
        }
        List<ServiceInstance> instances = discoveryClient.getInstances(serviceId);
        if(instances == null || instances.isEmpty()){
            //isEmpty [send email notify]
            logger.error("\n>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> \n" +
                    ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>\n" +
                    ">>>>>>>>>>>>>>>>>  【 current service isEmpty 】\n " +
                    ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>\n" +
                    ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
            return null;
        }
        int size = instances.size();
        if(size == 1){
            return instances.get(0);
        }

        return routerEum.getRouter().getServiceInstance(instances);
    }
}
