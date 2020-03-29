package com.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 在启动时初始化bean，此时申请一块内存区域作为缓存区，因此所有用户共享此块缓存区，在进行缓存的时候，需带有用户标识的字符
 *
 * @param <K>
 * @param <T>
 */
@Component
public class CacheService<K, T> implements InitializingBean {

    /**
     * 初始化一块内存区域
     * 设置最大存储数量为1000
     */
    private volatile Cache<K, T> cache = CacheBuilder.newBuilder().maximumSize(1000).build();

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

    /**
     * 删除缓存
     * @param k
     */
    public void deleteValue(K k) {
        this.cache.invalidate(k);
    }

    /**
     * 清空缓存区
     */
    public void clear() {
        this.cache.invalidateAll();
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }
}
