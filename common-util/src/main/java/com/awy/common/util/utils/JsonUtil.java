package com.awy.common.util.utils;


import cn.hutool.core.date.DatePattern;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Slf4j
public final class JsonUtil {

    private static ObjectMapper OBJECTMAPPER = new ObjectMapper();

    private JsonUtil() {
        //no instance
    }

    static {
        //设置java.util.Date时间类的序列化以及反序列化的格式
        OBJECTMAPPER.setDateFormat(new SimpleDateFormat(DatePattern.NORM_DATETIME_PATTERN));

        // 初始化JavaTimeModule
        JavaTimeModule javaTimeModule = new JavaTimeModule();

        //处理LocalDateTime
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN);
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(dateTimeFormatter));
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(dateTimeFormatter));

        //处理LocalDate
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN);
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(dateFormatter));
        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(dateFormatter));

        //处理LocalTime
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern(DatePattern.NORM_TIME_PATTERN);
        javaTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(timeFormatter));
        javaTimeModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer(timeFormatter));

        //注册时间模块, 支持支持jsr310, 即新的时间类(java.time包下的时间类)
        OBJECTMAPPER.registerModule(javaTimeModule);

        // 包含所有字段
        OBJECTMAPPER.setSerializationInclusion(JsonInclude.Include.ALWAYS);

        // 在序列化一个空对象时时不抛出异常
        OBJECTMAPPER.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

        // 忽略反序列化时在json字符串中存在, 但在java对象中不存在的属性
        OBJECTMAPPER.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    public static ObjectMapper getObjectMapper(){
        return OBJECTMAPPER;
    }

    /**
     * 序列化格式话的json字符
     * @param object 对象
     * @param <T> T
     * @return json字符
     */
    public static <T> String toJson(T object){
        if (object==null){
            return null;
        }
        try {
            return object instanceof String ? (String) object:OBJECTMAPPER.writeValueAsString(object);
        } catch (IOException e) {
            log.warn("Parse object to string error",e);
            return null;
        }
    }

    /**
     * 序列化格式话的json字符
     * @param object 对象
     * @param <T> T
     * @return 格式话json字符
     */
    public static <T> String toJsonPretty(T object){
        if (object==null){
            return null;
        }
        try {
            return object instanceof String ? (String) object:OBJECTMAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(object);
        } catch (IOException e) {
            log.warn("Parse object to string error",e);
            return null;
        }
    }

    public static <T> T fromJson(String str,Class<T> clazz){
        if (StrUtil.isEmpty(str) || clazz == null){
            return null;
        }
        try{
            return clazz.equals(String.class) ? (T) str:OBJECTMAPPER.readValue(str,clazz);
        }catch (Exception e){
            log.warn("Parse String to object error!",e);
            return null;
        }
    }
    public static <T> T string2Object(String str, TypeReference<T> typeReference){
        if (StrUtil.isEmpty(str) || typeReference == null){
            return null;
        }
        try{
            return typeReference.getType().equals(String.class) ? (T)str:OBJECTMAPPER.readValue(str,typeReference);
        }catch (Exception e){
            log.warn("Parse String to object error!",e);
            return null;
        }
    }
    public static <T> T string2Object(String str,Class<?> collectionClass,Class<?>... elementClass){
        JavaType javaType = OBJECTMAPPER.getTypeFactory().constructParametricType(collectionClass,elementClass);
        try{
            return OBJECTMAPPER.readValue(str,javaType);
        }catch (Exception e){
            log.warn("Parse String to object error!",e);
            return null;
        }
    }

}
