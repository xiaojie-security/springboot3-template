package com.security.backend.filter;

import cn.hutool.core.util.StrUtil;
import com.security.backend.properties.SecurityProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 令牌解析过滤器。
 */
@Slf4j
@RequiredArgsConstructor
public class ResolveTokenFilter extends OncePerRequestFilter {

    /**
     * 安全配置属性。
     */
    private final SecurityProperties securityProperties;

    /**
     * 解析请求头中的令牌，并将规范化后的令牌传递给下游过滤器。
     *
     * @param request 请求对象
     * @param response 响应对象
     * @param filterChain 过滤器链
     * @throws ServletException Servlet异常
     * @throws IOException IO异常
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        SecurityProperties.Certificate access = securityProperties.getAccess();
        String header = access.getHeader();
        String token = request.getHeader(header);
        if (StrUtil.isEmpty(token)) {
            filterChain.doFilter(request, response);
            return;
        }
        String prefix = access.getPrefix();
        if (StrUtil.isNotEmpty(prefix) && token.startsWith(prefix)) {
            token  = StrUtil.subAfter(token, prefix, true);
        }
        token = StrUtil.trim(token);
        request.setAttribute(header, token);
        filterChain.doFilter(request, response);
    }
}
