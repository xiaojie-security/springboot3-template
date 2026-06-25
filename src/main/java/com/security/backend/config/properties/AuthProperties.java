package com.security.backend.config.properties;

import cn.hutool.crypto.SecureUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "application.auth")
@Data
@Slf4j
public class AuthProperties implements InitializingBean {

    /**
     * 需要排除 JWT 认证拦截的 URL 列表
     */
    private List<String> excludeUrl;

    @Override
    public void afterPropertiesSet() throws Exception {
        log.debug("AuthProperties.afterPropertiesSet 加载成功");
    }

}
