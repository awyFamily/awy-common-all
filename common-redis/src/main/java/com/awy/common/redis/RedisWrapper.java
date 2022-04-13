package com.awy.common.redis;

import cn.hutool.core.util.StrUtil;
import com.awy.common.redis.data.ScanData;
import com.awy.common.util.utils.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.BitFieldSubCommands;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * @author yhw
 */
@Component
public class RedisWrapper {

    @Autowired
    private  RedisTemplate redisTemplate;

    public RedisTemplate getRedisTemplate(){
        return redisTemplate;
    }


    //============================== 字符串操作 =====================================

    /**
     * 获取缓存字符
     * @param key 缓存key
     * @return 缓存字符
     */
    public String  getStr(String key){
        return getStringTemplate().get(key);
    }

    /**
     * 获取缓存字符
     * @param key 缓存key
     * @param supplier 提供者
     * @return 缓存字符
     */
    public String getStrAndSet(String key, Supplier<String> supplier) {
        String result = getStringTemplate().get(key);
        if (result == null) {
            result = supplier.get();
            if (result != null) {
                this.setStr(key,result);
            }
        }
        return result;
    }

    /**
     * 获取缓存字符
     * @param key 缓存key
     * @param timeOut 超时时间
     * @param timeUnit 超时单位
     * @param supplier 提供者
     * @return 缓存字符
     */
    public String getStrAndSetEx(String key, long timeOut, TimeUnit timeUnit, Supplier<String> supplier) {
        String result = getStringTemplate().get(key);
        if (result == null) {
            result = supplier.get();
            if (result != null) {
                this.setStrEx(key,result,timeOut,timeUnit);
            }
        }
        return result;
    }


    /**
     * 持久化保存
     * @param key
     * @param value
     */
    public void setStr(String key,String value){
        getStringTemplate().set(key,value);
    }


    /**
     * 超时保存
     * @param key 缓存key
     * @param value 缓存值
     * @param secondsTimeOut 过期秒数
     */
    public void setStrEx(String key,String value,long secondsTimeOut){
        setStrEx(key, value, secondsTimeOut, TimeUnit.SECONDS);
    }


    /**
     * 超时保存
     * @param key 缓存key
     * @param value 缓存值
     * @param timeOut 过期秒数
     * @param timeUnit 时间单位
     */
    public void setStrEx(String key,String value,long timeOut,TimeUnit timeUnit){
        getStringTemplate().set(key, value, timeOut, timeUnit);
    }

    private ValueOperations<String, String> getStringTemplate(){
        return redisTemplate.opsForValue();
    }


    //====================== object =======================================

    /**
     * 获取缓存对象
     * @param key 缓存key
     * @param clazz 类型
     * @param <T> 类型
     * @return 缓存对象
     */
    public <T> T  getObj(String key,Class<T> clazz){
        String value = this.getStr(key);
        return JsonUtil.fromJson(value,clazz);
    }

    /**
     * 获取缓存对象
     * @param key 缓存key
     * @param clazz 类型
     * @param supplier 提供者
     * @param <T> 类型
     * @return 缓存对象
     */
    public <T> T getObjAndSet(String key, Class<T> clazz, Supplier<T> supplier) {
        T obj = this.getObj(key, clazz);
        if (obj == null) {
            obj = supplier.get();
            if (obj != null) {
                this.setObj(key,obj);
            }
        }
        return obj;
    }

    /**
     * 获取缓存对象
     * @param key 缓存key
     * @param clazz 类型
     * @param timeOut 超时时间
     * @param timeUnit 超时单位
     * @param supplier 提供者
     * @param <T> 类型
     * @return 缓存对象
     */
    public <T> T getObjAndSetEx(String key, Class<T> clazz, long timeOut, TimeUnit timeUnit, Supplier<T> supplier) {
        T obj = this.getObj(key, clazz);
        if (obj == null) {
            obj = supplier.get();
            if (obj != null) {
                this.setObjEx(key,obj,timeOut,timeUnit);
            }
        }
        return obj;
    }

    public void setObj(String key,Object value){
        this.setStr(key, JsonUtil.toJson(value));
    }

    /**
     * 保存的对象，必须要实现序列化接口
     **/
    public void setObjEx(String key,Object value,long timeOut){
        setObjEx(key,value,timeOut,TimeUnit.SECONDS);
    }

    public void setObjEx(String key,Object value,long timeOut,TimeUnit timeUnit){
        this.setStrEx(key, JsonUtil.toJson(value),timeOut,timeUnit);
    }

    //=============  atomic operator ======================

    public Long decrement(String key) {
        return this.getStringTemplate().decrement(key);
    }

    public Long decrement(String key, long delta) {
        return this.getStringTemplate().decrement(key,delta);
    }

    public Long decrementEx(String key,final long timeout) {
        return this.decrementEx(key,1,timeout);
    }

    public Long decrementEx(String key, long delta, final long timeout) {
        return this.decrementEx(key,timeout,delta,TimeUnit.SECONDS);
    }

    public Long decrementEx(String key, long delta, final long timeout, final TimeUnit unit) {
        String exists = this.getStr(key);
        Long result = (delta == 1) ? this.decrement(key) : this.decrement(key,delta);
        if (StrUtil.isBlank(exists)) {
            this.expire(key,timeout,unit);
            return result;
        }
        return result;
    }

    public Long increment(String key) {
        return this.getStringTemplate().increment(key);
    }

    public Long increment(String key, long delta) {
        return this.getStringTemplate().increment(key,delta);
    }

    public Long incrementEx(String key,final long timeout) {
        return this.incrementEx(key,1,timeout);
    }

    public Long incrementEx(String key, long delta, final long timeout) {
        return this.incrementEx(key,timeout,delta,TimeUnit.SECONDS);
    }

    public Long incrementEx(String key, long delta, final long timeout, final TimeUnit unit) {
        //expire <- ttl = -2 , forever <- ttl = -1
        String exists = this.getStr(key);
        Long result = (delta == 1) ? this.increment(key) : this.increment(key,delta);
        if (StrUtil.isBlank(exists)) {
            this.expire(key,timeout,unit);
            return result;
        }
        return result;
    }

    //========================================


    //===================== map ================================
    public Map getMap(String key){
        return getHashTemplate().entries(key);
    }


    public void setMap(String key,Map map){
        getHashTemplate().putAll(key,map);
    }



    public HashOperations getHashTemplate(){
        return redisTemplate.opsForHash();
    }


    //======================= other =========================


    public ListOperations getListTemplate(){
        return redisTemplate.opsForList();
    }

    public <T> void rightPush(String key,T value) {
        this.getListTemplate().rightPush(key,value);
    }

    public <T> void rightPushEx(String key,T value, final long timeout, final TimeUnit unit) {
        this.getListTemplate().rightPush(key,value);
        this.redisTemplate.expire(key,timeout,unit);
    }

    public <T> void rightPushAllEx(String key,List<T> values, final long timeout, final TimeUnit unit) {
        this.getListTemplate().rightPush(key,values);
        this.redisTemplate.expire(key,timeout,unit);
    }

    public <T> void leftPush(String key,T value) {
        this.getListTemplate().leftPush(key,value);
    }

    public <T> void leftPushEx(String key,T value, final long timeout, final TimeUnit unit) {
        this.getListTemplate().leftPush(key,value);
        this.redisTemplate.expire(key,timeout,unit);
    }

    public <T> void leftPushAllEx(String key,List<T> values, final long timeout, final TimeUnit unit) {
        this.getListTemplate().leftPushAll(key,values);
        this.redisTemplate.expire(key,timeout,unit);
    }

    public <T> List<T> getList(String key, long start, long end) {
        return (List<T>)this.getListTemplate().range(key, start, end);
    }

    public <T> List<T> getAllList(String key) {
        return (List<T>)this.getListTemplate().range(key, 0, -1);
    }

    public void removeListValue(String key,Object value) {
        this.removeListValue(key,value,0);
    }

    public void removeListValue(String key,Object value,long count) {
        this.getListTemplate().remove(key,count,value);
    }

    public SetOperations getSetTemplate(){
        return redisTemplate.opsForSet();
    }


    public ZSetOperations getZsetTemplate(){
        return redisTemplate.opsForZSet();
    }

    public void expire(String key,final long timeout, final TimeUnit unit) {
        this.redisTemplate.expire(key,timeout,unit);
    }

    //================================ hyperLogLog ============================================
    public HyperLogLogOperations getHyperLogLog(){
        return redisTemplate.opsForHyperLogLog();
    }


    //============================= 删除操作 =======================================


    /**
     * 删除单个
     * 在 4.0 版本引入了 unlink 指令，它能对删除操作进行懒处理，丢给后台线程来异步回收内存
     * @param key
     */
    public void delKey(String key){
        redisTemplate.unlink(key);
    }


    /**
     * 多个删除
     * @param keys
     */
    public void delKey(String... keys){
        Set<String> set = Stream.of(keys).collect(Collectors.toSet());
        this.delKey(set);
    }

    /**
     * 删除key集合
     * @param keys
     */
    public void delKey(Collection<String> keys){
        if(keys != null && !keys.isEmpty()){
            if(keys instanceof Set){
            }else {
                keys = keys.stream().collect(Collectors.toSet());
            }
            //判断 redis 版本
            redisTemplate.unlink(keys);
        }
    }

    /**
     * 模糊删除(传入前置key)
     * @param prefixKey
     */
    public void delLikeKey(String prefixKey){
        this.delKey(this.keys(prefixKey.concat("*")));
    }


    //========================== scan (槽位) -> limit  =======================================

    /**
     * scan 操作，需要测试
     * @param pattern 匹配的 key 值前缀
     * @return
     */
    public ScanData scan(String pattern) {
        return this.scan(pattern,1000);
    }

    public ScanData scan(String pattern, Integer count) {
        return this.scan(pattern,null,count);
    }

    /**
     *  scan cursor [MATCH pattern] [COUNT count]
     * @param pattern 匹配的 key 值前缀
     * @param scanData scan 数据对象
     * @param count  单次遍历字典的槽位
     * @return
     */
    public ScanData scan(String pattern, ScanData scanData,Integer count) {
        if(scanData == null){
            scanData = new ScanData("0",new ArrayList<>());
        }
        RedisScript<List> redisScript = RedisScript.of(
                "return redis.call('scan',KEYS[1],'MATCH',ARGV[1],'count',ARGV[2])", List.class);

        RedisSerializer serializer = redisTemplate.getStringSerializer();
        List<Object> list = (List<Object>)redisTemplate.execute(redisScript, serializer,
                serializer, Collections.singletonList(pattern.concat("*")), scanData.getCursor(), count);

        scanData.setCursor((String)list.get(0));
        scanData.setKeys((List<String>) list.get(1));
        return scanData;
    }

    /**
     * 获取符合条件的key
     * @param pattern	表达式
     * @return
     */
    public List<String> keys(String pattern) {
        ScanData scanData = new ScanData("0",new ArrayList<>());
        do {
            scanData = this.scan(pattern,scanData,1000);
        }while ((!"0".equals(scanData.getCursor())));

        return scanData.getKeys();
    }


    //======================================  位图 ======================================

    public boolean getBit(String key, long offset){
        return this.getStringTemplate().getBit(key,offset);
    }

    public boolean setBit(String key, long offset, boolean value){
        return this.getStringTemplate().setBit(key,offset,value);
    }

    /**
     * 位图批量操作，无符号位操作
     * unsigned 无符号位(正数)
     * signed 有符号位(负数)
     * @param key key
     * @param bits 需要获取多少字节(最大64)
     * @param offset 起始位置
     * @return 通过得到的十进制转换为 2进制就是 图
     */
    public Long uBitField(String key,int bits,long offset){
        if(bits > 64){
            bits = 63;
        }
        List<Long> longs = this.bitField(key, bits,offset,false);
        if(longs == null || longs.isEmpty()){
            return null;
        }
        return longs.get(0);
    }

    /**
     * 位图，符号位操作
     * unsigned 无符号位(正数)
     * signed 有符号位(负数)
     * @param key key
     * @param bits 需要获取多少字节(最大64)
     * @param offset 起始位置
     * @return 通过得到的十进制转换为 2进制就是 图
     */
    public Long iBitField(String key,int bits,long offset){
        if(bits > 64){
            bits = 64;
        }
        List<Long> longs = this.bitField(key, bits,offset,true);
        if(longs == null || longs.isEmpty()){
            return null;
        }
        return longs.get(0);
    }

    /**
     * 位图
     * @param key
     * @param bits 需要获取多少字节
     * @param offset 起始位置
     * @param signed 是否符号位
     * @return 通过得到的十进制转换为 2进制就是 图
     */
    private List<Long> bitField(String key,int bits,long offset,boolean signed){
        BitFieldSubCommands bitFieldSubCommands = BitFieldSubCommands.create();
        if(bits < 1){
            bits = 1;
        }

        BitFieldSubCommands.BitFieldType bitFieldType;
        if(signed){
            bitFieldType = BitFieldSubCommands.BitFieldType.signed(bits);
        }else {
            bitFieldType = BitFieldSubCommands.BitFieldType.unsigned(bits);
        }

        bitFieldSubCommands = bitFieldSubCommands.get(bitFieldType).valueAt(offset);
        return this.getStringTemplate().bitField(key, bitFieldSubCommands);
    }

}
