package com.awy.common.util.utils;


import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public final class JsonUtil {

    private static final ObjectMapper mapper = new ObjectMapper();


    private JsonUtil() {
        //no instance
    }

    public static <T> T fromJson(String jsonStr, Class<T> clazz) {
        try {
            return mapper.readValue(jsonStr,clazz);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String toJson(Object object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ObjectMapper getObjectMapper(){
        return mapper;
    }

}
