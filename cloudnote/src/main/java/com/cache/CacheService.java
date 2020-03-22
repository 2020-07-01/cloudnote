package com.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;


@Component
public class CacheService<K, T> {

    /**
     * 初始化一块内存区域
     * 设置最大存储数量为100
     * 设置缓存过期时间为10分钟
     */
    private volatile Cache<K, T> cache = CacheBuilder.newBuilder().maximumSize(100).expireAfterWrite(60 * 10, TimeUnit.SECONDS).build();

    /**
     * 存储信息
     *
     * @param k
     * @param t
     */
    public void putValue(K k, T t) {
        this.cache.put(k, t);
    }

    /**
     * 获取信息
     */
    public T getValue(K k) {
        return this.cache.getIfPresent(k);
    }
}
