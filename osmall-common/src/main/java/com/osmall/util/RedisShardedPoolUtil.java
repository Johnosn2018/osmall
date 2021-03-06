package com.osmall.util;

import com.osmall.common.RedisShardedPool;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.ShardedJedis;

/**
 * Created by Johnson on 2018/10/7.
 */
@Slf4j
public class RedisShardedPoolUtil {


    /**
     *设置key的有效期，单位是秒
     * @param key
     * @param exTime
     * @return
     */
    public static Long expire(String key,int exTime){
        ShardedJedis jedis=null;
        Long result=null;

        try {
            jedis= RedisShardedPool.getJedis();
            result=jedis.expire(key,exTime);
        } catch (Exception e) {
            log.error("expire key:{}  error",key,e);
            RedisShardedPool.returnBrokenResource(jedis);
            return result;
        }
        RedisShardedPool.returnResource(jedis);
        return result;
    }


    //exTime的单位是秒
    public static String setEx(String key,String value,int exTime){
        ShardedJedis jedis=null;
        String result=null;

        try {
            jedis= RedisShardedPool.getJedis();
            result=jedis.setex(key,exTime,value);
        } catch (Exception e) {
            log.error("setex key:{} value:{} error",key,value,e);
            RedisShardedPool.returnBrokenResource(jedis);
            return result;
        }
        RedisShardedPool.returnResource(jedis);
        return result;
    }


    public static String set(String key,String value){
        ShardedJedis jedis=null;
        String result=null;

        try {
            jedis= RedisShardedPool.getJedis();
            result=jedis.set(key,value);
        } catch (Exception e) {
            log.error("set key:{} value:{} error",key,value,e);
            RedisShardedPool.returnBrokenResource(jedis);
            return result;
        }
        RedisShardedPool.returnResource(jedis);
        return result;
    }

    public static String get(String key){
        ShardedJedis jedis=null;
        String result=null;

        try {
            jedis= RedisShardedPool.getJedis();
            result=jedis.get(key);
        } catch (Exception e) {
            log.error("get key:{}  error",key,e);
            RedisShardedPool.returnBrokenResource(jedis);
            return result;
        }
        RedisShardedPool.returnResource(jedis);
        return result;
    }


    public static Long del(String key){
        ShardedJedis jedis=null;
        Long result=null;

        try {
            jedis= RedisShardedPool.getJedis();
            result=jedis.del(key);
        } catch (Exception e) {
            log.error("get key:{}  error",key,e);
            RedisShardedPool.returnBrokenResource(jedis);
            return result;
        }
        RedisShardedPool.returnResource(jedis);
        return result;
    }


    public static void main(String[] args) {
        ShardedJedis jedis=RedisShardedPool.getJedis();

       RedisShardedPoolUtil.set("keyTest3","value");

        String value= RedisShardedPoolUtil.get("keyTest3");

        //RedisShardedPoolUtil.setEx("keyex","valueex",60*10);

        //RedisShardedPoolUtil.expire("keyTest",60*20);

        //RedisShardedPoolUtil.del("keyTest");

       System.out.println(value);

        System.out.println("end");

    }

}
