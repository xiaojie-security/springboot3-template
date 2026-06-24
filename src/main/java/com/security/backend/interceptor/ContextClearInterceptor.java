package com.security.backend.interceptor;

import com.security.backend.context.ContextHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 请求完成后的上下文清理拦截器。
 */
@Slf4j
public class ContextClearInterceptor implements HandlerInterceptor , InitializingBean {

    @Override
    public void afterPropertiesSet() throws Exception {
        log.debug("Interceptor 'ContextClearInterceptor' configured for use");
    }

    /**
     * 请求完成后清理线程上下文。
     *
     * @param request 请求对象
     * @param response 响应对象
     * @param handler 处理器
     * @param ex 异常信息
     * @throws Exception 清理过程中发生异常
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex)
            throws Exception {
        ContextHolder.clear();
    }
}
