package com.awy.common.util.utils;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ReflectUtil;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author yhw
 */
@Slf4j
public class ReflexUtils extends ReflectUtil{


    /**
     * 获取对象所有方法，已剔除 serialVersionUID
     * @param t
     * @return 方法集合
     */
    public static <T> List<Field>  getClassAllField(Class<T> t){
        List<Field> list = new ArrayList<>();
        try {
            return getClassAllField(t,list);
        } catch (Exception e) {
            log.error( "entity conversion error ");
        }
        return list;
    }

    /**
     *  获取实体所有属性
     * @param t 实体对象类名
     * @param ignoreColumn 忽略的属性名
     * @return 属性集合
     */
    public static <T> List<Field> getClassAllField(Class<T> t,String... ignoreColumn){
        List<Field> list = getClassAllField(t);
        if(ignoreColumn!=null && ignoreColumn.length>0 &&list!=null && !list.isEmpty()) {
            list = list.stream().filter(field->{
                boolean blan = true;
                for (String str : ignoreColumn) {
                    if(str.equals(field.getName())) {
                        blan =  false;
                    }
                }
                return blan;
            }).collect(Collectors.toList());
        }
        return list;
    }

    /***
     * 自动忽略javax.persistence.Transient 注解的属性(后期需要加上)
     * @param t 类名
     * @param fields 方法集合
     * @return 所有方法
     */
    private static <T> List<Field> getClassAllField(Class<T> t, List<Field> fields){
        T obj =  newInstance(t);
        if(!"java.lang.Object".equals(obj.getClass().getSuperclass().getName())) {
            getClassAllField(obj.getClass().getSuperclass(),fields);
        }
        fields.addAll(Stream.of(obj.getClass().getDeclaredFields()).collect(Collectors.toList()));

        if(fields!=null&&!fields.isEmpty()) {

            fields = fields.stream().filter
                    (field->!"serialVersionUID".equals(field.getName()))
//                    field.getAnnotation(Transient.class)==null)
                    .distinct().collect(Collectors.toList());
        }
        return fields;
    }

    /**
     * 反射获取方法内容
     * @param method 方法
     * @param obj 对象类名
     * @return method return content
     */
    public static <T> Object getMethodInvoke(Method method, T obj) {
        try {
            return method.invoke(obj);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 反射获取方法
     * @param methodName 方法名
     * @param t 对象类名
     * @return 方法
     */
    public static <T> Method getMethod(String methodName,T t) {
        Class<? extends Object> clazz = t.getClass();
        return getMethod(methodName,clazz,null);
    }

    private static <T> Method getMethod(String methodName,Class<? extends Object> clazz,Method method) {
        if(!"java.lang.Object".equals(clazz.getName())) {
            if(method==null) {
                try {
                    method = clazz.getDeclaredMethod(methodName);
                } catch (NoSuchMethodException e) {//getSuperclass
//					e.printStackTrace();
                    method = getMethod(methodName,clazz.getSuperclass(),method);
                } catch (SecurityException e) {
                    e.printStackTrace();
                }
            }
        }
        if(method==null) {
            try {
                throw new Exception(clazz.getName() + " don't have menthod --> " + methodName);
            } catch (Exception e) {
//				e.printStackTrace();
            }
        }
        return method;
    }

    /**
     * 获取值
     * @param value 值对象
     * @return 值
     */
    public static Object getValue(Object value) {
        if(value!=null) {
            if(value.getClass() == Boolean.class || value.getClass() == boolean.class) {
                return "true".equals(value.toString()) ? 0 : 1;
            }

            if(value.getClass() == LocalDateTime.class) {
                LocalDateTime localDateTime = (LocalDateTime)value;
                return "'"+ localDateTime.format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN))+"'";
            }
            if(value.getClass() == LocalDate.class) {
                LocalDate localDate = (LocalDate)value;
                return "'"+ localDate.format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN))+"'";
            }
            if(value.getClass() == Date.class) {
                return "'"+ DateUtil.format((Date)value, DatePattern.NORM_DATETIME_PATTERN)+"'";
            }
            if(value.getClass() == java.sql.Timestamp.class) {
                return "'"+DateUtil.format((Date)value, DatePattern.NORM_DATETIME_PATTERN)+"'";
            }
            if(value.getClass() == String.class) {
//				return "'"+value.toString().replaceAll("'", "\'")+"'";
                return "'"+value.toString().replaceAll("'", "`")+"'";
            }
            return "'"+value.toString()+"'";
        }
        return "NULL";
    }

}

