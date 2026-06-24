package com.security.backend.cache.impl;


import cn.hutool.json.JSONUtil;
import com.security.backend.cache.CacheLoader;
import com.security.backend.cache.CacheService;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;

public class RedisCacheService implements CacheService {

    private final StringRedisTemplate redisTemplate;

    public RedisCacheService(
            StringRedisTemplate redisTemplate) {

        this.redisTemplate = redisTemplate;
    }

    @Override
    public <T> T get(String key, Class<T> clazz) {

        String json = redisTemplate.opsForValue().get(key);

        if (json == null) {
            return null;
        }

        try {
            return JSONUtil.toBean(json, clazz);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void set(String key, Object value) {

        try {
            redisTemplate.opsForValue()
                    .set(key, JSONUtil.toJsonStr(value));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void set(String key, Object value, Duration ttl) {

        try {
            redisTemplate.opsForValue()
                    .set(
                            key,
                            JSONUtil.toJsonStr(value),
                            ttl
                    );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean setIfAbsent(String key, Object value) {

        try {
            Boolean success = redisTemplate.opsForValue()
                    .setIfAbsent(key, JSONUtil.toJsonStr(value));
            return Boolean.TRUE.equals(success);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean setIfAbsent(String key, Object value, Duration ttl) {

        try {
            Boolean success = redisTemplate.opsForValue()
                    .setIfAbsent(
                            key,
                            JSONUtil.toJsonStr(value),
                            ttl
                    );
            return Boolean.TRUE.equals(success);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean exists(String key) {
        return redisTemplate.hasKey(key);
    }

    @Override
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    @Override
    public <T> T getOrLoad(
            String key,
            Class<T> clazz,
            Duration ttl,
            CacheLoader<T> loader) {

        T value = get(key, clazz);

        if (value != null) {
            return value;
        }

        value = loader.load();

        if (value != null) {
            set(key, value, ttl);
        }

        return value;
    }

    @Override
    public <T> T getOrLoad(String key, Class<T> clazz, CacheLoader<T> loader) {
        T value = get(key, clazz);

        if (value != null) {
            return value;
        }

        value = loader.load();

        if (value != null) {
            set(key, value);
        }

        return value;
    }
}
