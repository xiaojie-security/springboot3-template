package com.security.backend.interceptor;

import cn.hutool.core.util.StrUtil;
import com.security.backend.cache.CacheService;
import com.security.backend.exception.BadRequestException;
import com.security.backend.handler.RedisKeysHandler;
import com.security.backend.handler.SysConfigHandler;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.Duration;

@Slf4j
public class NonceInterceptor implements HandlerInterceptor, InitializingBean {

    private static final String NONCE_HEADER = "X-Nonce";

    @Resource
    private SysConfigHandler sysConfigHandler;

    @Resource
    private CacheService cacheService;

    @Override
    public void afterPropertiesSet() throws Exception {
        log.debug("Interceptor 'NonceInterceptor' configured for use");
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) {

        Long nonceExpireSeconds = sysConfigHandler.queryNonceExpireSeconds();
        if (nonceExpireSeconds == null || nonceExpireSeconds <= 0) {
            return true;
        }

        String nonce = request.getHeader(NONCE_HEADER);

        if (StrUtil.isEmpty(nonce)) {
            log.warn("NonceInterceptor.preHandle 请求随机串不能为空");
            throw new BadRequestException("请求随机串不能为空");
        }

        boolean success = cacheService.setIfAbsent(
                RedisKeysHandler.getSecurityNonce(nonce),
                "1",
                Duration.ofSeconds(nonceExpireSeconds)
        );

        if (!success) {
            log.warn("NonceInterceptor.preHandle 检测到重放攻击, uri={}, nonce={}", request.getRequestURI(), nonce);
            throw new BadRequestException("请求已重复提交");
        }

        return true;
    }


}
