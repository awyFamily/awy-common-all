package com.awy.common.gateway.compose;

import cn.hutool.json.JSONUtil;
import lombok.AllArgsConstructor;
import org.springframework.cloud.gateway.config.GatewayProperties;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.support.NameUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * swagger 资源提供
 * 参考： https://blog.csdn.net/ttzommed/article/details/81103609
 */
@Primary
@Component
@AllArgsConstructor
public class SwaggerProvider implements SwaggerResourcesProvider {

    public static final String API_URI = "/v2/api-docs";
    private final  RouteLocator routeLocator;
    private final  GatewayProperties gatewayProperties;

    @Override
    public List<SwaggerResource> get() {


        List<SwaggerResource> resources = new ArrayList<>();
        List<String> routes = new ArrayList<>();

        routeLocator.getRoutes().subscribe(obj->routes.add(obj.getId()));

        gatewayProperties.getRoutes().stream()/*.peek(obj -> {
            System.out.println("=================peek======================");

            System.out.println(obj.toString());

            System.out.println("=================peek======================");
        })*/.filter(obj -> routes.contains(obj.getId()))
                .forEach(routeDefinition -> routeDefinition.getPredicates().stream()
                        .filter(predicateDefinition -> "Path".equalsIgnoreCase(predicateDefinition.getName()))
                        .filter(predicateDefinition -> !"auth".equalsIgnoreCase(routeDefinition.getId()))
                        .forEach(predicateDefinition -> resources.add(swaggerResource(routeDefinition.getId(),
                                predicateDefinition.getArgs().get(NameUtils.GENERATED_NAME_PREFIX + "0")
                                        .replace("/**", API_URI)))));

//        System.out.println(JSONUtil.toJsonStr(resources));
//        System.out.println(JSONUtil.toJsonStr(routes));
        return resources;
    }


    private SwaggerResource swaggerResource(String name, String location) {
        SwaggerResource swaggerResource = new SwaggerResource();
        swaggerResource.setName(name);
        swaggerResource.setLocation(location);
        swaggerResource.setSwaggerVersion("2.0");
        return swaggerResource;
    }


}
