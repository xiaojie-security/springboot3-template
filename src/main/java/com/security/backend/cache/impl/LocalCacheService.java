package com.security.backend.cache.impl;


import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.security.backend.cache.CacheLoader;
import com.security.backend.cache.CacheService;

import java.time.Duration;

public class LocalCacheService implements CacheService {

    private final Cache<String, Object> cache;

    public LocalCacheService() {
        this.cache = Caffeine.newBuilder()
                .maximumSize(10000)
                .build();
    }

    @Override
    public <T> T get(String key, Class<T> clazz) {
        Object value = cache.getIfPresent(key);
        return value == null ? null : clazz.cast(value);
    }

    @Override
    public void set(String key, Object value) {
        cache.put(key, value);
    }

    @Override
    public void set(String key, Object value, Duration ttl) {
        cache.put(key, value);
        // Caffeine的TTL一般在Builder配置
    }

    @Override
    public boolean setIfAbsent(String key, Object value) {
        return cache.asMap().putIfAbsent(key, value) == null;
    }

    @Override
    public boolean setIfAbsent(String key, Object value, Duration ttl) {
        return cache.asMap().putIfAbsent(key, value) == null;
    }

    @Override
    public boolean exists(String key) {
        return cache.getIfPresent(key) != null;
    }

    @Override
    public void delete(String key) {
        cache.invalidate(key);
    }

    @Override
    public <T> T getOrLoad(
            String key,
            Class<T> clazz,
            Duration ttl,
            CacheLoader<T> loader) {

        Object value = cache.get(key, k -> loader.load());

        return value == null ? null : clazz.cast(value);
    }

    @Override
    public <T> T getOrLoad(String key, Class<T> clazz, CacheLoader<T> loader) {
        Object value = cache.get(key, k -> loader.load());

        return value == null ? null : clazz.cast(value);
    }
}
