package com.security.backend.config.properties;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "application.token")
@Data
@Slf4j
public class TokenProperties implements InitializingBean {

    /**
     * Access Token 认证配置
     */
    private Certificate access;

    /**
     * Refresh Token 刷新配置
     */
    private Certificate refresh;

    @Override
    public void afterPropertiesSet() throws Exception {
        if (StrUtil.isNotEmpty(this.access.secret)) {
            this.access.secret = SecureUtil.md5(this.access.secret);
        }
        if (StrUtil.isNotEmpty(this.refresh.secret)) {
            this.refresh.secret = SecureUtil.md5(this.refresh.secret);
        }
        log.debug("TokenProperties.afterPropertiesSet 加载成功");
    }

    /**
     * JWT 认证信息内部类
     */
    @Data
    public static class Certificate {

        /**
         * JWT 签名密钥（原始配置值）
         */
        private String secret;

        /**
         * JWT 令牌请求头前缀
         */
        private String prefix;

        /**
         * JWT 令牌请求头名称
         */
        private String header;
    }
}
