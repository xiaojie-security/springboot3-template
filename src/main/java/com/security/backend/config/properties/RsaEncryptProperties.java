package com.security.backend.config.properties;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "application.encrypt.rsa")
@Data
@Slf4j
public class RsaEncryptProperties implements InitializingBean {

    /**
     * RSA 私钥字符串（PKCS#8 格式）
     * <p>
     * 用于 JWT 令牌的签名生成，必须与公钥配对使用。
     * 支持 PEM 格式的 Base64 编码字符串。
     * </p>
     */
    private String privateKey;

    /**
     * RSA 公钥字符串（X.509 格式）
     * <p>
     * 用于 JWT 令牌的签名验证，必须与私钥配对使用。
     * 支持 PEM 格式的 Base64 编码字符串。
     * </p>
     */
    private String publicKey;

    @Override
    public void afterPropertiesSet() throws Exception {
        log.debug("RsaEncryptProperties.afterPropertiesSet 加载成功");
    }
}
