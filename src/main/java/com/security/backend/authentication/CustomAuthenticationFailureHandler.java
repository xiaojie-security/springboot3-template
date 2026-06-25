package com.security.backend.authentication;


import cn.hutool.http.HttpStatus;
import com.security.backend.utils.HttpServletUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        log.error("AuthenticationFailureHandlerImpl.onAuthenticationFailure 认证失败 请求路径={} 错误信息={}",request.getRequestURI(), exception.getMessage());
        new HttpServletUtils(response).writeResponseBody(HttpStatus.HTTP_UNAUTHORIZED, "unauthorized 认证失败 请检查凭证是否正确");
    }
}
