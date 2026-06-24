package com.security.backend.filter;

import com.security.backend.wrapper.RepeatableRequestWrapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 包装请求类 可重复读取流
 */
@Slf4j
public class RequestCachingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        RepeatableRequestWrapper wrappedRequest = new RepeatableRequestWrapper(request);
        filterChain.doFilter(wrappedRequest, response);
    }
}
