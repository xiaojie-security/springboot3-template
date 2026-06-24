package com.security.backend.interceptor;

import com.security.backend.exception.BusinessException;
import com.security.backend.handler.SysConfigHandler;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
public class SystemMaintenanceInterceptor  implements HandlerInterceptor , InitializingBean {

    @Resource
    private SysConfigHandler sysConfigHandler;


    @Override
    public void afterPropertiesSet() throws Exception {
        log.debug("Interceptor 'SystemMaintenanceInterceptor' configured for use");
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        boolean maintenance = sysConfigHandler.enableSystemMaintenance();
        if (maintenance) {
            log.debug("SystemMaintenanceInterceptor.preHandle 系统维护模式生效禁止访问");
            throw new BusinessException("系统维护中暂不可使用");
        }
        return true;
    }
}
