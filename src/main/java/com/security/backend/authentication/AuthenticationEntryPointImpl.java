package com.security.backend.authentication;

import cn.hutool.http.HttpStatus;
import com.security.backend.utils.HttpUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

/**
 * 处理未认证请求的逻辑
 **/
@Slf4j
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint {
    /**
     *
     * @param request 请求体
     * @param response 响应体
     * @param authException 异常信息
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws ServletException {
        log.error("AuthenticationEntryPointImpl.commence 请求未认证 请求路径={} 错误信息={}",request.getRequestURI(), authException.getMessage());
        HttpUtils.writeResponseBody(response, HttpStatus.HTTP_UNAUTHORIZED, "unauthorized 认证失败");
    }
}
