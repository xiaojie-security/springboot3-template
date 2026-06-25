package com.security.backend.filter;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpStatus;

import com.security.backend.constant.RequestAttributeConstant;
import com.security.backend.context.ContextHolder;
import com.security.backend.handler.JwtTokenHandler;
import com.security.backend.properties.SecurityProperties;
import com.security.backend.utils.HttpServletUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * token 过滤拦截器
 */
@Slf4j
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    @Resource
    private SecurityProperties securityProperties;

    @Resource
    private JwtTokenHandler jwtTokenHandler;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        HttpServletUtils servletUtils = new HttpServletUtils(request,response);
        String accessToken = servletUtils.getAttributeSafe(securityProperties.getAccess().getHeader(),String.class);

        // 判断token是否为空，如果为空则放行请求（允许匿名访问）
        if (StrUtil.isEmpty(accessToken)) {
            // 继续执行过滤器链，放行请求
            filterChain.doFilter(request,response);
            // 直接返回，不再执行后续逻辑
            return;
        }

        String accessSecretKey = securityProperties.getAccess().getSecret();
        String username;
        Long userId;

        try {
            username = jwtTokenHandler.getUsername(accessToken, accessSecretKey);
            userId = jwtTokenHandler.getUserId(accessToken, accessSecretKey,Long.class);
        } catch (Exception e) {
            log.debug("TokenAuthenticationFilter.doFilterInternal 获取accessToken失败",e);
            servletUtils.writeResponseBody(HttpStatus.HTTP_UNAUTHORIZED, "unauthorized 身份校验失败 请重新登入");
            return;
        }
        // 验证用户名是否为空，如果为空说明token无效
        if (StrUtil.isEmptyIfStr(username)) {
            log.debug("TokenAuthenticationFilter.doFilterInternal 获取用户名为空");
            servletUtils.writeResponseBody(HttpStatus.HTTP_UNAUTHORIZED, "unauthorized 身份校验失败 请重新登入");
            return;
        }
        // 验证用户ID是否为空，如果为空说明token无效
        if (ObjUtil.isEmpty(userId)) {
            log.debug("TokenAuthenticationFilter.doFilterInternal 获取用户ID为空");
            servletUtils.writeResponseBody(HttpStatus.HTTP_UNAUTHORIZED, "unauthorized 身份校验失败 请重新登入");
            return;
        }

        // 判断Redis中是否存在该用户的token，如果不存在说明token已过期或用户已登出
        if (!jwtTokenHandler.validate(accessToken, accessSecretKey)) {
            log.debug("TokenAuthenticationFilter.doFilterInternal accessToken已过期");
            servletUtils.writeResponseBody(HttpStatus.HTTP_UNAUTHORIZED, "unauthorized 身份标识已过期 请重新登入");
            return;
        }

        request.setAttribute(RequestAttributeConstant.USERNAME, username);
        request.setAttribute(RequestAttributeConstant.ACCESS_TOKEN, accessToken);
        request.setAttribute(RequestAttributeConstant.USER_ID, userId);

        filterChain.doFilter(request, response);

    }

}
