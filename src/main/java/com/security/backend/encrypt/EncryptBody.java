package com.security.backend.encrypt;

import lombok.Data;

/**
 * 混合加密传输体。
 */
@Data
public class EncryptBody {

    /**
     * AES密钥，通过RSA公钥加密。
     */
    private String secret;

    /**
     * AES-GCM初始化向量。
     */
    private String iv;

    /**
     * AES-GCM加密后的数据。
     */
    private String data;
}
