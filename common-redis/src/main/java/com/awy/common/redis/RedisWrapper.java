package com.awy.common.redis;

import com.awy.common.redis.data.ScanData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.BitFieldSubCommands;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * @author yhw
 */
@Component
public class RedisWrapper {

    protected static final ObjectMapper MAPPER = new ObjectMapper();

    @Autowired
    private  RedisTemplate redisTemplate;

    public RedisTemplate getRedisTemplate(){
        return redisTemplate;
    }


    //============================== 字符串操作 =====================================

    public String  getStr(String key){
        return getStringTemplate().get(key);
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
     * @param key
     * @param value
     * @param secondsTimeOut 过期秒数
     */
    public void setStrEx(String key,String value,long secondsTimeOut){
        setStrEx(key, value, secondsTimeOut, TimeUnit.SECONDS);
    }


    /**
     * 超时保存
     * @param key
     * @param value
     * @param timeOut
     * @param timeUnit
     */
    public void setStrEx(String key,String value,long timeOut,TimeUnit timeUnit){
        getStringTemplate().set(key, value, timeOut, timeUnit);
    }

    private ValueOperations<String, String> getStringTemplate(){
        return redisTemplate.opsForValue();
    }


    //====================== object =======================================

    public <T> T  getObj(String key,Class<T> clazz){
        String value = this.getStr(key);
        try {
            return MAPPER.readValue(value,clazz);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setObj(String key,Object value){
        try {
            this.setStr(key, MAPPER.writeValueAsString(value));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存的对象，必须要实现序列化接口
     **/
    public void setObjEx(String key,Object value,long timeOut){
        setObjEx(key,value,timeOut,TimeUnit.SECONDS);
    }

    public void setObjEx(String key,Object value,long timeOut,TimeUnit timeUnit){
        try {
            this.setStrEx(key, MAPPER.writeValueAsString(value),timeOut,timeUnit);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }




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




    public SetOperations getSetTemplate(){
        return redisTemplate.opsForSet();
    }




    public ZSetOperations getZsetTemplate(){
        return redisTemplate.opsForZSet();
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




    /*    public void test(String key){
        ScanOptions scanOptions = ScanOptions.scanOptions().match(prefixKey).count(100).build();
        redisTemplate.execute(new RedisCallback<String>(){
            @Override
            public String doInRedis(RedisConnection connection) throws DataAccessException {
                RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
                connection.scan()
                byte[] res = connection.rPop(serializer.serialize(key));
                return serializer.deserialize(res);
            }
        });
    }*/
}
