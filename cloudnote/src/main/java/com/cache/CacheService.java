package com.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 在启动时初始化bean，此时申请一块内存区域作为缓存区，因此所有用户共享此块缓存区，在进行缓存的时候，需带有用户标识的字符
 *
 * 所有用户共同使用一块缓存
 * <accountId  Map<>>
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
    private volatile LoadingCache<K, T> cache;

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
     *
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

    /**
     * 初始化bean之前执行
     *
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        cache = CacheBuilder.newBuilder().build(new CacheLoader<K, T>() {
            //在缓存不存在时通过此方法加载缓存,在初始化时此方法不执行
            @Override
            public T load(K T) throws Exception {

                System.out.println("1321321");
                return null;
            }
        });
    }
}
