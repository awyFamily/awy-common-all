package com.awy.common.discovery.client.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * @author yhw
 */
public class RestTemplateUtil {

    private static Logger logger = LoggerFactory.getLogger(RestTemplateUtil.class);


    public static <R> R getForObj(RestTemplate restTemplate,String path,Class<R> responseType){
        return getForObj(restTemplate,path,"",responseType);
    }

    public static <R> R getForObj(RestTemplate restTemplate,String path,String  author,Class<R> responseType){
        return getForObj(restTemplate,path,getHeaderForm(author),responseType);
    }

    public static <R> R getForObj(RestTemplate restTemplate,String path,MultiValueMap<String, String> headers,Class<R> responseType){
        return getRequest(restTemplate,path,headers,responseType);
    }

    public static <R> R getRequest(RestTemplate restTemplate,String path,MultiValueMap<String, String> headers,Class<R> responseType,Object... uriVariables){
        return request(restTemplate,HttpMethod.GET,path,null,headers,responseType,uriVariables);
    }

    //================================ post request ===================================================

    public static <T,R> R postForObj(RestTemplate restTemplate,String path,String author,T request,Class<R> responseType){
        return postForObj(restTemplate,path,request,getHeaderForm(author),responseType);
    }

    public static <T,R> R postForObjBody(RestTemplate restTemplate,String path,T request,Class<R> responseType){
        HttpHeaders header = new HttpHeaders();
        header.set(HttpHeaders.CONTENT_TYPE,MediaType.APPLICATION_JSON_UTF8_VALUE);
        return request(restTemplate,HttpMethod.POST,path,request,header,responseType);
    }

    public static <T,R> R postForObj(RestTemplate restTemplate,String path,T request,MultiValueMap<String, String> headers,Class<R> responseType){

        // ==== restTemplate.postForObject(path,getHttpEntity(request,headers),responseType) ===

        return postRequest(restTemplate,path,request,headers,responseType);
    }

    public static <T,R> R postRequest(RestTemplate restTemplate,String path,T request,MultiValueMap<String, String> headers,Class<R> responseType,Object... uriVariables){
        return request(restTemplate,HttpMethod.POST,path,request,headers,responseType,uriVariables);
    }

    //===================================== other ================================================================


    private static <T> HttpEntity getHttpEntity(T obj,MultiValueMap<String, String> headers){
        if(headers == null || headers.isEmpty()){
            return new HttpEntity(obj);
        }
        return new HttpEntity(obj,headers);
    }


    public static HttpHeaders getHeaderForm(String author){
        if(author == null || author.isEmpty()){
            return null;
        }
        HttpHeaders header = new HttpHeaders();
        header.set("Authorization",author);
        header.set(HttpHeaders.CONTENT_TYPE,MediaType.APPLICATION_FORM_URLENCODED_VALUE);
        return header;
    }

    public static HttpHeaders setHeaderByJson(String author){
        if(author == null || author.isEmpty()){
            return null;
        }
        HttpHeaders header = new HttpHeaders();
        header.set(HttpHeaders.CONTENT_TYPE,MediaType.APPLICATION_JSON_UTF8_VALUE);
        header.set("Authorization",author);
        return header;
    }

    public static <T,R> ResponseEntity<R> getResponseEntity(RestTemplate restTemplate,HttpMethod method,String path,HttpEntity httpEntity,Class<R> responseType,Object... uriVariables){
        if(restTemplate == null){
            logger.error(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> \t" +
                    ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> \t" +
                    ">>>>>>>>>>>>>>>>>>>>>>>>>>>>> 【 restTemplate isEmpty 】\t" +
                    ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> \t" +
                    ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
            return null;
        }
        return restTemplate.exchange(path,method,httpEntity,responseType,uriVariables);
    }

    public static <T,R> R request(RestTemplate restTemplate,HttpMethod method,String path,T request,MultiValueMap<String, String> headers,Class<R> responseType,Object... uriVariables){
        ResponseEntity<R> responseEntity = getResponseEntity(restTemplate,method,path,getHttpEntity(request,headers),responseType,uriVariables);
        if(responseEntity == null){
            return null;
        }
        return responseEntity.getBody();
    }
}
