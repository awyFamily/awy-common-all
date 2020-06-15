package com.yhw.nc.common.redis;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;

public final class RedisConnectionTest {



    public static void main(String[] args) throws Exception{
        RedisURI redisURI = io.lettuce.core.RedisURI.create("127.0.0.1", 6379);
        redisURI.setPassword("123456");
        redisURI.setDatabase(0);
        RedisClient redisClient = RedisClient.create(redisURI);





//        RedisClient redisClient = RedisClient.create("redis://localhost");

        StatefulRedisConnection<String, String> connect = redisClient.connect();
/*        RedisAsyncCommands<String, String> async = connect.async();
        RedisFuture<String> abc1 = async.get("abc");
        abc1.get(30, TimeUnit.SECONDS);*/

        RedisCommands<String, String> sync = connect.sync();
        String abc = sync.get("abc");
        System.out.println(abc);
    }

}
