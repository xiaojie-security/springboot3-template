package com.security.backend.filter;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.http.HttpStatus;
import com.security.backend.constant.RequestAttributeConstant;
import com.security.backend.enums.DeviceType;
import com.security.backend.properties.SecurityProperties;
import com.security.backend.utils.HttpServletUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
public class RequestLegalFilter extends OncePerRequestFilter {

    @Resource
    SecurityProperties securityProperties;

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        HttpServletUtils requestUtils = new HttpServletUtils(request);

        List<String> excludeUrl = securityProperties.getExcludeUrl();

        String requestUri = request.getRequestURI();
        if (CollUtil.isNotEmpty(excludeUrl)) {
            for (String url : excludeUrl) {
                if (pathMatcher.match(url, requestUri)) {
                    filterChain.doFilter(request, response);
                    return;
                }
            }
        }

        boolean requestValid = requestUtils.isRequestValid();
        if (!requestValid) {
            log.error("RequestLegalityFilter.doFilterInternal 请求不合法");
            new HttpServletUtils(response).writeResponseBody(HttpStatus.HTTP_BAD_REQUEST, "非法请求");
            return;
        }
        String deviceId = requestUtils.getDeviceId();
        DeviceType deviceType = requestUtils.getDeviceType();
        String ipv4 = requestUtils.getIpv4();
        String ipv6 = requestUtils.getIpv6();
        String userAgent = requestUtils.getUserAgent();

        request.setAttribute(RequestAttributeConstant.DEVICE_ID, deviceId);
        request.setAttribute(RequestAttributeConstant.DEVICE_TYPE, deviceType);
        request.setAttribute(RequestAttributeConstant.IPV4, ipv4);
        request.setAttribute(RequestAttributeConstant.IPV6, ipv6);
        request.setAttribute(RequestAttributeConstant.USER_AGENT, userAgent);

        filterChain.doFilter(request,response);

    }
}
