package com.security.backend.properties;

import cn.hutool.crypto.SecureUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 安全配置属性类
 * <p>
 * 用于加载 application.yml 或 application.properties 中以 {@code security} 为前缀的配置项。
 * 主要包括 JWT 认证相关的配置信息，如令牌密钥、过期时间、排除路径等。
 * </p>
 *
 * <p>配置示例（application.yml）：</p>
 * <pre>
 * security:
 *   exclude-url:
 *     - /login
 *     - /register
 *     - /public/**
 *   access:
 *     secret: accessSecretKey
 *     expire: 3600
 *     prefix: Bearer
 *     header: Authorization
 *   refresh:
 *     secret: refreshSecretKey
 *     expire: 7200
 *     prefix: Bearer
 *     header: Authorization
 *   rsa-private-key: MIIEvQIBADANBgkqhkiG9w0BAQEFAASC...
 *   rsa-public-key: MIIBCgKCAQEAu1SU1LfVLPHC...
 * </pre>
 *
 * @author security-team
 * @version 1.0.0
 * @since 2026-06-17
 */
@ConfigurationProperties(prefix = "security")
@Data
@Slf4j
public class SecurityProperties implements InitializingBean {

    /**
     * 需要排除 JWT 认证拦截的 URL 列表
     * <p>
     * 配置在 security.exclude-url 下，支持 Ant 风格路径匹配表达式。
     * 这些路径将不受 JWT 认证过滤器的保护，可直接访问。
     * </p>
     */
    private List<String> excludeUrl;

    /**
     * Access Token 认证配置
     * <p>
     * 用于访问接口的短期令牌配置，包含密钥、过期时间、请求头前缀等信息。
     * </p>
     */
    private Certificate access;

    /**
     * Refresh Token 刷新配置
     * <p>
     * 用于刷新 Access Token 的长期令牌配置，包含密钥、过期时间、请求头前缀等信息。
     * Refresh Token 的有效期通常比 Access Token 更长。
     * </p>
     */
    private Certificate refresh;

    /**
     * RSA 私钥字符串（PKCS#8 格式）
     * <p>
     * 用于 JWT 令牌的签名生成，必须与公钥配对使用。
     * 支持 PEM 格式的 Base64 编码字符串。
     * </p>
     */
    private String rsaPrivateKey;

    /**
     * RSA 公钥字符串（X.509 格式）
     * <p>
     * 用于 JWT 令牌的签名验证，必须与私钥配对使用。
     * 支持 PEM 格式的 Base64 编码字符串。
     * </p>
     */
    private String rsaPublicKey;

    /**
     * 属性初始化回调方法
     * <p>
     * 在 Spring 容器设置完所有属性后调用。
     * 对 {@link Certificate#secret} 配置的简易密钥进行 MD5 二次加密处理，
     * 以提高 JWT 签名密钥的安全性。
     * </p>
     *
     * <p>注意：由于 JJWT 0.12.3 版本对密钥安全性要求提升，
     * 建议配置的密钥长度至少为 256 位（32 字节）。
     * 通过 MD5 加密可将任意长度密钥转化为 128 位（16 字节）固定长度密钥。</p>
     *
     * @throws Exception 如果密钥处理过程中发生错误
     * @see SecureUtil#md5(String)
     * @since 1.0.0
     */
    @Override
    public void afterPropertiesSet() throws Exception {

        // 获取配置的认证密钥
        String accessSecretKey = this.access.secret;
        String refreshSecretKey = this.refresh.secret;

        // 使用 MD5 加密算法对简易密钥进行二次加密（32 位十六进制字符串）
        this.access.secret = SecureUtil.md5(accessSecretKey);
        this.refresh.secret = SecureUtil.md5(refreshSecretKey);

        log.info("SecurityProperties.afterPropertiesSet 安全配置初始化成功");
    }

    /**
     * JWT 认证信息内部类
     * <p>
     * 封装单个令牌（Access Token 或 Refresh Token）的认证配置信息。
     * </p>
     *
     * @author security-team
     * @since 1.0.0
     */
    @Data
    public static class Certificate {

        /**
         * JWT 签名密钥（原始配置值）
         * <p>
         * 配置在 application.yml 中的原始密钥字符串。
         * 在 {@link SecurityProperties#afterPropertiesSet()} 中会被 MD5 加密处理。
         * </p>
         * <p>建议至少配置 32 位以上的复杂字符串。</p>
         */
        private String secret;

        /**
         * JWT 令牌请求头前缀
         * <p>
         * 配置在 HTTP 请求头中传递令牌时的前缀标识。
         * 常见值为 {@code Bearer}。
         * </p>
         * <p>例如：请求头 {@code Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...}</p>
         */
        private String prefix = "Bearer ";

        /**
         * JWT 令牌请求头名称
         * <p>
         * 配置客户端在 HTTP 请求中携带令牌的 Header 名称。
         * 常见值为 {@code Authorization}。
         * </p>
         */
        private String header = "Authorization";
    }


}


