package com.security.backend.authentication;



import com.security.backend.domain.UserPrincipal;
import com.security.backend.handler.JwtTokenHandler;
import com.security.backend.handler.SysConfigHandler;
import com.security.backend.properties.SecurityProperties;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthenticationSuccessHandlerImpl implements AuthenticationSuccessHandler{
    private final SecurityProperties securityProperties;
    private final SysConfigHandler sysConfigHandler;
    private final StringRedisTemplate redisTemplate;
    private final JwtTokenHandler jwtTokenHandler;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        String username = principal.getUsername();
        Long userId = principal.getUserId();
        log.info("AuthenticationSuccessHandlerImpl.onAuthenticationSuccess 认证成功 请求路径={} 用户名={}",request.getRequestURI(), username);
        // 1. 生成token
        String accessToken = jwtTokenHandler.createAccessToken(username, userId, securityProperties.getAccess().getSecret());
        String refreshToken = jwtTokenHandler.createRefreshToken(securityProperties.getRefresh().getSecret());

        boolean singleLogin = sysConfigHandler.enableSingleLogin();
    }


}
