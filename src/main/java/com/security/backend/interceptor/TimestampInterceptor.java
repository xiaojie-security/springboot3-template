package com.security.backend.interceptor;

import com.security.backend.exception.BadRequestException;
import com.security.backend.handler.SysConfigHandler;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
public class TimestampInterceptor implements HandlerInterceptor , InitializingBean {

    /**
     * 请求头名称
     */
    private static final String TIMESTAMP_HEADER = "X-Timestamp";

    @Resource
    private SysConfigHandler sysConfigHandler;

    @Override
    public void afterPropertiesSet() throws Exception {
        log.debug("Interceptor 'TimestampInterceptor' configured for use");
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // 1. 获取防重放超时时间（秒）
        Long allowedTimeDiffSeconds = sysConfigHandler.queryReplayAttackTimeoutSeconds();
        if (allowedTimeDiffSeconds == null || allowedTimeDiffSeconds <= 0) {
            // 未配置或配置值无效，跳过校验
            return true;
        }

        // 2. 获取请求头中的时间戳
        String timestampStr = request.getHeader(TIMESTAMP_HEADER);
        if (timestampStr == null || timestampStr.isBlank()) {
            log.warn("TimestampInterceptor.preHandle 请求时间戳为空, uri={}", request.getRequestURI());
            throw new BadRequestException("请求时间戳不能为空");
        }

        // 3. 解析时间戳
        long requestTimestampSeconds;
        try {
            requestTimestampSeconds = Long.parseLong(timestampStr);
        } catch (NumberFormatException e) {
            log.error("TimestampInterceptor.preHandle 请求时间戳格式错误, uri={}, timestampStr={}",
                    request.getRequestURI(), timestampStr, e);
            throw new BadRequestException("请求时间戳格式错误，请使用Unix时间戳（秒）");
        }

        // 4. 获取当前时间戳（秒）
        long currentTimestampSeconds = System.currentTimeMillis() / 1000;

        // 5. 计算时间差（秒）
        long diffSeconds = Math.abs(currentTimestampSeconds - requestTimestampSeconds);

        // 6. 校验时间差是否在允许范围内
        if (diffSeconds > allowedTimeDiffSeconds) {
            log.warn("TimestampInterceptor.preHandle 请求时间戳校验失败, uri={}, requestTimestamp={}, currentTimestamp={}, diff={}s, allowed={}s",
                    request.getRequestURI(), requestTimestampSeconds, currentTimestampSeconds,
                    diffSeconds, allowedTimeDiffSeconds);
            throw new BadRequestException("请求已过期");
        }
        return true;
    }

}
