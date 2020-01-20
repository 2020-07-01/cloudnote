package com.Redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @program: RedisUtil
 * @description: reids工具类
 * @create: 2020-01-16 14:00
 **/
public class RedisUtil {

    @Autowired
    RedisTemplate redisTemplate;




    public boolean set(String key, String value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean set(String key, String value, int expireTime) {
        try {
            redisTemplate.opsForValue().set(key, value, expireTime);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean hasKey(String key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(String key) {
        try {
            return redisTemplate.delete(key);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


}
