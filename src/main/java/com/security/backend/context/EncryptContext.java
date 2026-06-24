package com.security.backend.context;

/**
 * 加密上下文。
 */
public class EncryptContext {

    /**
     * 当前请求AES密钥。
     */
    private static final ThreadLocal<String> aesSecretLocal = new ThreadLocal<>();

    protected EncryptContext() {}

    /**
     * 设置当前请求AES密钥。
     *
     * @param secret AES密钥
     */
    public void setAesSecret(String secret) {
        aesSecretLocal.set(secret);
    }

    /**
     * 获取当前请求AES密钥。
     *
     * @return AES密钥
     */
    public String getAesSecret() {
        return aesSecretLocal.get();
    }

    /**
     * 清理当前请求加密上下文。
     */
    public void clear() {
        aesSecretLocal.remove();
    }

}
