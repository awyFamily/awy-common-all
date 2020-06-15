package com.yhw.nc.common.discovery.client;

import com.yhw.nc.common.discovery.client.util.RestTemplateUtil;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

public class RestTemplateTest {

    public static void main(String[] args) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "192.168.88.159:8083/admin/listAllUsersByCondition";
        String auth = "bearer a0967080-6f65-4c9a-b910-21db15c4261b";
        Map<String,Object> requtst = new HashMap<>();
        Map map = RestTemplateUtil.postForObj(restTemplate, url, auth, requtst, Map.class);
        System.out.println(map);
    }
}
