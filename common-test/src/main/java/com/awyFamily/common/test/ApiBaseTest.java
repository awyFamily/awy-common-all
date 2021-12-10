package com.awyFamily.common.test;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import org.junit.After;
import org.junit.Before;

import java.util.Collection;
import java.util.Map;

/**
 * @author yhw
 * @date 2021-12-10
 */
public abstract class ApiBaseTest {


    /**
     * 接口入参
     */
    private static JSON parameters;

    /**
     * 响应结果
     */
    public static String responseResult = "";

    /**
     * 开始执行时间
     */
    private long startExecutorTime;

    /**
     * 启用通用请求
     * 文件上传则进行设置为 false
     */
    public static boolean enableCommonRequest = true;
    /**
     * 是否 post 请求
     */
    public static boolean hasPost  = true;
    /**
     * post 请求 是否是 body 数据
     */
    public static boolean hasBody  = true;
    /**
     * 是否传递认证信息
     */
    public static boolean hasAuthor  = true;
    /**
     * 默认的用户名
     */
    public static String username = "";
    /**
     * 默认的密码
     */
    public static String password = "";

    /**
     * 基础地址
     * @return
     */
    public abstract String getBaseUrl();
    /**
     * token 地址
     */
    public abstract String getAuthUrl();

    /**
     * 服务基础地址
     */
    public abstract String getServiceUrl();

    public String url = getServiceUrl();

    public abstract String getTokenStr(String userName,String password);



    @Before
    public void before(){
        startExecutorTime = System.currentTimeMillis();
    }

    @After
    public void After(){
        System.out.println("request >>>>>>>> url:  \n\t" + this.url);
        System.out.println("\nrequest >>>>>>>> parameter:");
        if (parameters != null)
            System.out.println(JSONUtil.formatJsonStr(parameters.toString()));
        if (enableCommonRequest) {
            sendCommonRequest();
        }
        System.out.println("\nrequest >>>>>>> Result:");
        System.out.println(JSONUtil.formatJsonStr(responseResult));
        System.out.println("接口耗时：" + (System.currentTimeMillis() - startExecutorTime) + " 毫秒");
    }

    /**
     * 通用请求
     */
    private void sendCommonRequest() {
        HttpRequest httpRequest;
        if (hasPost){
            httpRequest = HttpRequest.post(this.url);
            if (hasBody) {
                httpRequest.body(parameters.toString());
            } else {
                if (parameters instanceof Map && CollUtil.isNotEmpty((Map) parameters))
                    httpRequest.form((Map<String, Object>) parameters);
            }
        } else {
            httpRequest = HttpRequest.get(this.url);
            if (parameters instanceof Map && CollUtil.isNotEmpty((Map) parameters))
                httpRequest.form((Map<String, Object>) parameters);
        }

        if (hasAuthor) {
            httpRequest.auth(getTokenStr(username,password));
        }

        responseResult = httpRequest.execute().body();
    }

    public ApiBaseTest post() {
        return this;
    }

    public ApiBaseTest get() {
        hasPost = false;
        return this;
    }

    public ApiBaseTest auth(boolean hasAuthor) {
        hasAuthor = hasAuthor;
        return this;
    }

    public ApiBaseTest auth(String username, String password) {
        ApiBaseTest.username = username;
        ApiBaseTest.password = password;
        hasAuthor = true;
        return this;
    }

    public ApiBaseTest setParameters(Object obj) {
        Assert.isFalse(obj == null, "参数为空");
        if (obj instanceof Collection) {
            parameters = JSONUtil.parseArray(obj);
        }else {
            parameters = JSONUtil.parseObj(obj);
        }
        return this;
    }

    public ApiBaseTest hasBody(boolean hasBody) {
        hasBody = hasBody;
        return this;
    }

    public ApiBaseTest concat(String url) {
        this.url = this.url.concat(url);
        return this;
    }
}
