package com.security.backend.authentication;

import cn.hutool.http.HttpStatus;
import com.security.backend.utils.HttpServletUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

/**
 *  配置用户请求被拒绝的逻辑
 **/
@Slf4j
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    /**
     *
     * @param request 请求体
     * @param response 响应体
     * @param accessDeniedException 请求拒绝异常信息
     */
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws ServletException {
        log.error("AccessDeniedHandlerImpl.handle 拒绝访问 请求路径={} 错误信息={}", request.getRequestURI(), accessDeniedException.getMessage());
        new HttpServletUtils(response).writeResponseBody(HttpStatus.HTTP_FORBIDDEN, "Access Denied 暂无权限访问");
    }
}
