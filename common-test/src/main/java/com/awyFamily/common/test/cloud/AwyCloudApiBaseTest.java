package com.awyFamily.common.test.cloud;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.awyFamily.common.test.ApiBaseTest;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yhw
 * @date 2021-12-10
 */
public abstract class AwyCloudApiBaseTest extends ApiBaseTest {

    @Override
    public String getBaseUrl() {
        return "";
    }

    @Override
    public String getAuthUrl() {
        return getBaseUrl().concat("/auth");
    }

    @Override
    public abstract String getServiceUrl();


    @Override
    public String getTokenStr(String userName, String password) {
        String tokenUrl = getAuthUrl().concat("/oauth/token");
        Map<String,Object> tokenParameters = new HashMap<>();
        tokenParameters.put("username",userName);
        tokenParameters.put("password", SecureUtil.md5(password));
        tokenParameters.put("scope","app");
        tokenParameters.put("grant_type","password");

        String result = HttpRequest.post(tokenUrl)
                .form(tokenParameters)
                .header("Authorization","Basic ".concat(Base64.getEncoder().encodeToString("client:secret".getBytes())))
                .execute()
                .body();

        JSONObject resultJson = JSONUtil.parseObj(result);
        String token_type = resultJson.getStr("token_type");
        String access_token = resultJson.getStr("access_token");

        return token_type.concat(" ").concat(access_token);
    }
}
