package com.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.checkerframework.checker.units.qual.K;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 在启动时初始化bean，此时申请一块内存区域作为缓存区，因此所有用户共享此块缓存区，在进行缓存的时候，需带有用户标识的字符
 * <p>
 * 所有用户共同使用一块缓存
 * 每个用户的缓存信息存储在Map中，Map由accountId唯一标识
 * <accountId  Map<key,value>>

 */
@Component
public class CacheService implements InitializingBean {

    /**
     * 初始化一块内存区域
     * 设置最大存储数量为1000
     */
    private volatile LoadingCache<String, Map> cache;

    /**
     * 存储信息
     */
    public void putValue(String string, Map map) {
        this.cache.put(string, map);
    }

    /**
     * 获取信息
     */
    public Map getValue(String string) {
        return this.cache.getIfPresent(string);
    }

    /**
     * 删除缓存
     */
    public void deleteValue(String string) {
        this.cache.invalidate(string);
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
        cache = CacheBuilder.newBuilder().build(new CacheLoader<String, Map>() {
            //在缓存不存在时通过此方法加载缓存,在初始化时此方法不执行
            @Override
            public Map load(String string) throws Exception {
                //System.out.println("1321321");
                return null;
            }
        });
    }
}
