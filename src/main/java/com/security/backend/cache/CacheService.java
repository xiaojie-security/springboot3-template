package com.security.backend.cache;


import java.time.Duration;

public interface CacheService {

    /**
     * 获取缓存
     */
    <T> T get(String key, Class<T> clazz);

    /**
     * 写入缓存（永久）
     */
    void set(String key, Object value);

    /**
     * 写入缓存（带过期时间）
     */
    void set(String key, Object value, Duration ttl);

    /**
     * 不存在则写入缓存（永久）。
     */
    boolean setIfAbsent(String key, Object value);

    /**
     * 不存在则写入缓存（带过期时间）。
     */
    boolean setIfAbsent(String key, Object value, Duration ttl);

    /**
     * 是否存在
     */
    boolean exists(String key);

    /**
     * 删除缓存
     */
    void delete(String key);

    /**
     * 获取并自动加载
     */
    <T> T getOrLoad(
            String key,
            Class<T> clazz,
            Duration ttl,
            CacheLoader<T> loader
    );

    /**
     * 获取并自动加载
     */
    <T> T getOrLoad(
            String key,
            Class<T> clazz,
            CacheLoader<T> loader
    );
}
