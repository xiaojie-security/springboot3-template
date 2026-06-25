package com.security.backend.filter;

import com.security.backend.constant.RequestAttributeConstant;
import com.security.backend.context.ContextHolder;
import com.security.backend.enums.DeviceType;
import com.security.backend.utils.HttpServletUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Slf4j
public class ContextAssemblyFilter extends OncePerRequestFilter {


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        HttpServletUtils servletUtils = new HttpServletUtils(request, response);
        String deviceId = servletUtils.getAttributeSafe(RequestAttributeConstant.DEVICE_ID, String.class);
        DeviceType deviceType = servletUtils.getAttributeSafe(RequestAttributeConstant.DEVICE_TYPE, DeviceType.class);
        String ipv4 = servletUtils.getAttributeSafe(RequestAttributeConstant.IPV4, String.class);
        String ipv6 = servletUtils.getAttributeSafe(RequestAttributeConstant.IPV6, String.class);
        String userAgent = servletUtils.getAttributeSafe(RequestAttributeConstant.USER_AGENT, String.class);

        // 1. 请求上下文信息
        ContextHolder.getRequestContext().setRequestInfo(deviceId, deviceType, ipv4, ipv6, userAgent);
        // 2. SpringSecurity上下文
        String username = servletUtils.getAttributeSafe(RequestAttributeConstant.USERNAME, String.class);
        String accessToken = servletUtils.getAttributeSafe(RequestAttributeConstant.ACCESS_TOKEN, String.class);
        Long userId = servletUtils.getAttributeSafe(RequestAttributeConstant.USER_ID, Long.class);
        SecurityContextHolder.getContext().setAuthentication(UsernamePasswordAuthenticationToken.authenticated(
                username, null, AuthorityUtils.NO_AUTHORITIES
        ));
        // 3. 用户上下文
        ContextHolder.getUserContext()
                .setUserInfo(userId, username, accessToken);
        filterChain.doFilter(request, response);
    }

    /**
     * 将权限字符串列表转换为Spring Security的授权对象列表
     *
     * @param permissions 权限编码列表
     * @return Spring Security授权对象列表
     */
    private List<SimpleGrantedAuthority> toAuthorities(List<String> permissions) {
        // 创建授权对象列表
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        // 遍历每个权限编码
        for (String permission : permissions) {
            // 将权限编码包装成SimpleGrantedAuthority对象并添加到列表中
            authorities.add(new SimpleGrantedAuthority(permission));
        }
        // 返回授权对象列表
        return authorities;
    }

}
