package com.awy.common.message.api.util;

import java.util.UUID;

public class IdUtil {

    public static String randomId() {
        return UUID.randomUUID().toString().split("-")[0];
    }

    public static String randomId16() {
        return UUID.randomUUID().toString().replace("-","").toUpperCase().substring(0,16);
    }
}
