package com.security.backend.config;

import com.security.backend.cache.CacheService;
import com.security.backend.cache.impl.LocalCacheService;
import com.security.backend.cache.impl.RedisCacheService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * 缓存配置类。
 * <p>
 * 根据 {@code system.cache.type} 配置选择本地缓存或 Redis 缓存实现。
 * </p>
 */
@Configuration
public class CacheConfiguration {

    /**
     * 创建本地缓存服务。
     *
     * @return 本地缓存服务
     */
    @Bean
    @ConditionalOnProperty(
            prefix = "application.cache",
            name = "type",
            havingValue = "local"
    )
    public CacheService localCacheService() {
        return new LocalCacheService();
    }

    /**
     * 创建 Redis 缓存服务。
     *
     * @param redisTemplate Redis 字符串模板
     * @return Redis 缓存服务
     */
    @Bean
    @ConditionalOnProperty(
            prefix = "application.cache",
            name = "type",
            havingValue = "redis"
    )
    public CacheService redisCacheService(StringRedisTemplate redisTemplate) {
        return new RedisCacheService(redisTemplate);
    }
}
