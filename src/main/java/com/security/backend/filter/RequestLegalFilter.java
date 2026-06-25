package com.security.backend.filter;

import cn.hutool.http.HttpStatus;
import com.security.backend.constant.RequestAttributeConstant;
import com.security.backend.enums.DeviceType;
import com.security.backend.utils.HttpServletUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
public class RequestLegalFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        HttpServletUtils requestUtils = new HttpServletUtils(request);
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
