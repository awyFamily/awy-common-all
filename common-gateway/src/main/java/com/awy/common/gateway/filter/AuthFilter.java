package com.awy.common.gateway.filter;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONObject;
import com.google.common.collect.Lists;
import com.awy.common.discovery.client.util.ServiceInstanceUtil;
import com.awy.common.gateway.config.AuthFilterProperties;
import com.awy.common.redis.RedisWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * gateWay authenticate filter
 * with this filter . the internal service oauth release all resource. with authentication
 * internal service only need to get the token. get the token info
 * @author yhw
 */
@Component
public class AuthFilter implements GlobalFilter, Ordered {

    private static Logger logger = LoggerFactory.getLogger(AuthFilter.class);

    @Resource
    private DiscoveryClient discoveryClient;

    @Resource
    private RestTemplate restTemplate;

    @Resource
    private RedisWrapper redisWrapper;

    @Resource
    private AuthFilterProperties authFilterProperties;

    private static final String CLIENT = "client_id";

    // -> DefaultAccessTokenConverter
    private static final String EXPIRES = "exp";



//    private final static String AUTH_INSTANCE = "server-auth";

//    private final static String CHECK_URI_SUFFIX = "/oauth/check_token";

    private List<String> ignoreUri = Lists.newArrayList();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        if(isIgnore(request)){
            return chain.filter(exchange);
        }
        if(!isAuthSuccess(request)){
            //is authenticate error then response client . authenticate error info
            return getResponse(exchange);
        }
        return chain.filter(exchange);
    }


    private boolean isIgnore(ServerHttpRequest request){
        if(authFilterProperties.getIgnoreAll()){
            return true;
        }

        List<String> releaseUrl = authFilterProperties.getReleaseUrl();
        if(releaseUrl == null || releaseUrl.isEmpty()){
            return false;
        }

        URI uri = request.getURI();
        PathMatcher matcher = new AntPathMatcher();
        for (String url : releaseUrl) {
            if(matcher.match(url,uri.getPath())){
                return true;
            }
        }
        return false;
    }

    /**
     * is authenticate success
     * @param request
     * @return
     */
    private boolean isAuthSuccess(ServerHttpRequest request){
        List<String> authorizations = request.getHeaders().get("Authorization");
        if(CollectionUtil.isEmpty(authorizations)){
            return false;
        }
        String authorization = authorizations.get(0);
        String[] tokens = authorization.split(" ");
        if(tokens.length < 2){
            return false;
        }

        String token = tokens[1];

        if(isNotExpires(token)){
            return true;
        }

        ServiceInstance serviceInstance = ServiceInstanceUtil.getServiceInstance(discoveryClient,authFilterProperties.getServiceId(),authFilterProperties.getRouterCode());
        if(serviceInstance == null){
            return false;
        }

        String path = serviceInstance.getUri().toString().concat(authFilterProperties.getCheckUriSuffix()).concat("?token=".concat(token));
        try{
            Map forObject = restTemplate.getForObject(path, Map.class);

            if(forObject == null){
                return false;
            }

            if(forObject.get(CLIENT) == null){
                return false;
            }

            Integer expires =  (Integer) forObject.get(EXPIRES);
            if(expires == null){
                return false;
            }

            setExpires(token,expires);
        }catch (Exception e){
            logger.error("server authenticate\n error ",e);
            return false;
        }
        return true;
    }

    private Boolean isNotExpires(String token){
        String str = redisWrapper.getStr(token);
        if("1".equals(str)){
            return true;
        }
        return false;
    }

    /**
     * timeout is expires reduce 60s
     * @param token
     * @param expires
     */
    private void setExpires(String token,long expires){
        expires = expires - (new Date().getTime() / 1000);
        if(expires > 120){
            expires = expires - 60;
            redisWrapper.setStrEx(token,"1",expires);
        }
    }


    private Mono<Void>  getResponse(ServerWebExchange exchange){
        ServerHttpResponse response = exchange.getResponse();
        JSONObject message = new JSONObject();
        message.put("success", false);
        message.put("data", "");
        message.put("message", "authenticate error");
        message.put("code", HttpStatus.UNAUTHORIZED.value());
        byte[] bits = message.toString().getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = response.bufferFactory().wrap(bits);
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        //Specify the encoding, otherwise it will be Chinese garbled in the browser.
        response.getHeaders().add("Content-Type", "text/plain;charset=UTF-8");
        return response.writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return -99;
    }

}
