package com.security.backend.encrypt;

import com.security.backend.config.properties.RsaEncryptProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import javax.crypto.spec.SecretKeySpec;

/**
 * 混合加密服务。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EncryptService implements InitializingBean {

    /**
     * RSA算法。
     */
    private static final String RSA_ALGORITHM = "RSA";

    /**
     * RSA-OAEP加密转换。
     */
    private static final String RSA_TRANSFORMATION = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";

    /**
     * AES算法。
     */
    private static final String AES_ALGORITHM = "AES";

    /**
     * AES-GCM加密转换。
     */
    private static final String AES_TRANSFORMATION = "AES/GCM/NoPadding";

    /**
     * GCM认证标签位数。
     */
    private static final int GCM_TAG_LENGTH = 128;

    /**
     * GCM推荐初始化向量字节数。
     */
    private static final int GCM_IV_LENGTH = 12;

    /**
     * 安全配置。
     */
    private final RsaEncryptProperties rsaEncryptProperties;

    /**
     * 安全随机数。
     */
    private final SecureRandom secureRandom = new SecureRandom();

    /**
     * RSA私钥。
     */
    private PrivateKey privateKey;

    /**
     * RSA公钥。
     */
    private PublicKey publicKey;

    @Override
    public void afterPropertiesSet() throws Exception {
        String privateKeyText = rsaEncryptProperties.getPrivateKey();
        String publicKeyText = rsaEncryptProperties.getPublicKey();

        if (!StringUtils.hasText(privateKeyText) || !StringUtils.hasText(publicKeyText)) {
            log.error("EncryptService.afterPropertiesSet RSA密钥配置不完整，请检查配置");
            throw new IllegalStateException("RSA密钥配置不完整");
        }

        try {
            KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
            this.privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKeyText)));
            this.publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyText)));
            log.debug("EncryptService.afterPropertiesSet 混合加密服务初始化成功");
        } catch (Exception e) {
            log.error("EncryptService.afterPropertiesSet 混合加密服务初始化失败", e);
            throw new RuntimeException("RSA初始化失败", e);
        }
    }

    /**
     * 使用RSA公钥加密数据。
     *
     * @param plainText 明文数据
     * @return Base64编码的密文
     */
    public String encryptWithPublicKey(String plainText) {
        if (!StringUtils.hasText(plainText)) {
            return plainText;
        }

        try {
            Cipher cipher = newRsaCipher(Cipher.ENCRYPT_MODE, publicKey);
            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            log.error("EncryptService.encryptWithPublicKey RSA公钥加密失败", e);
            throw new RuntimeException("数据加密失败", e);
        }
    }

    /**
     * 使用RSA私钥解密数据。
     *
     * @param cipherText Base64编码的密文
     * @return 解密后的明文
     */
    public String decryptWithPrivateKey(String cipherText) {
        if (!StringUtils.hasText(cipherText)) {
            return cipherText;
        }

        try {
            Cipher cipher = newRsaCipher(Cipher.DECRYPT_MODE, privateKey);
            byte[] plainBytes = cipher.doFinal(Base64.getDecoder().decode(cipherText));
            return new String(plainBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("EncryptService.decryptWithPrivateKey RSA私钥解密失败，密文：{}", cipherText.substring(0, Math.min(20, cipherText.length())) + "...");
            throw new RuntimeException("数据解密失败", e);
        }
    }

    /**
     * 使用AES-GCM密钥加密数据。
     *
     * @param plainText 明文数据
     * @param secret Base64编码的AES密钥
     * @return Base64编码的密文
     */
    public String encryptWithAes(String plainText, String secret) {
        String iv = generateIv();
        return encryptWithAes(plainText, secret, iv);
    }



    /**
     * 使用AES-GCM密钥加密数据。
     *
     * @param plainText 明文数据
     * @param secret Base64编码的AES密钥
     * @param iv Base64编码的初始化向量
     * @return Base64编码的密文
     */
    public String encryptWithAes(String plainText, String secret, String iv) {
        if (!StringUtils.hasText(plainText)) {
            return plainText;
        }
        if (!StringUtils.hasText(secret)) {
            log.error("EncryptService.encryptWithAes AES密钥为空");
            throw new IllegalArgumentException("AES密钥为空");
        }

        try {
            Cipher cipher = newAesCipher(Cipher.ENCRYPT_MODE, secret, iv);
            byte[] cipherBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(cipherBytes);
        } catch (Exception e) {
            log.error("EncryptService.encryptWithAes AES加密失败", e);
            throw new RuntimeException("数据加密失败", e);
        }
    }

    public EncryptResult encryptWithAesByEncryptResult(String plainText, String secret) {
        String iv = generateIv();
        return new EncryptResult(iv,encryptWithAes(plainText, secret, iv));
    }

    /**
     * 使用AES-GCM密钥解密数据。
     *
     * @param cipherText Base64编码的密文
     * @param secret Base64编码的AES密钥
     * @param iv Base64编码的初始化向量
     * @return 明文数据
     */
    public String decryptWithAes(String cipherText, String secret, String iv) {
        if (!StringUtils.hasText(cipherText)) {
            return cipherText;
        }
        if (!StringUtils.hasText(secret)) {
            log.error("EncryptService.decryptWithAes AES密钥为空");
            throw new IllegalArgumentException("AES密钥为空");
        }

        try {
            Cipher cipher = newAesCipher(Cipher.DECRYPT_MODE, secret, iv);
            byte[] plainBytes = cipher.doFinal(Base64.getDecoder().decode(cipherText));
            return new String(plainBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("EncryptService.decryptWithAes AES解密失败", e);
            throw new RuntimeException("数据解密失败", e);
        }
    }

    /**
     * 获取当前使用的公钥。
     *
     * @return Base64格式的公钥
     */
    public String getPublicKey() {
        return rsaEncryptProperties.getPublicKey();
    }

    /**
     * 重置RSA实例。
     *
     * @throws Exception 初始化异常
     */
    public void reloadRsa() throws Exception {
        afterPropertiesSet();
        log.info("EncryptService.reloadRsa RSA实例已重新加载");
    }

    /**
     * 创建RSA Cipher。
     *
     * @param mode 加密或解密模式
     * @param key RSA密钥
     * @return Cipher实例
     * @throws Exception 初始化异常
     */
    private Cipher newRsaCipher(int mode, java.security.Key key) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA_TRANSFORMATION);
        OAEPParameterSpec oaepParameterSpec = new OAEPParameterSpec(
                "SHA-256",
                "MGF1",
                MGF1ParameterSpec.SHA256,
                PSource.PSpecified.DEFAULT
        );
        cipher.init(mode, key, oaepParameterSpec);
        return cipher;
    }

    /**
     * 创建AES-GCM Cipher。
     *
     * @param mode 加密或解密模式
     * @param secret Base64编码的AES密钥
     * @param iv Base64编码的初始化向量
     * @return Cipher实例
     * @throws Exception 初始化异常
     */
    private Cipher newAesCipher(int mode, String secret, String iv) throws Exception {
        byte[] secretBytes = Base64.getDecoder().decode(secret);
        byte[] ivBytes = Base64.getDecoder().decode(iv);
        Cipher cipher = Cipher.getInstance(AES_TRANSFORMATION);
        cipher.init(mode, new SecretKeySpec(secretBytes, AES_ALGORITHM), new GCMParameterSpec(GCM_TAG_LENGTH, ivBytes));
        return cipher;
    }

    /**
     * 生成AES-GCM初始化向量。
     *
     * @return Base64编码的初始化向量
     */
    private String generateIv() {
        byte[] iv = new byte[GCM_IV_LENGTH];
        secureRandom.nextBytes(iv);
        return Base64.getEncoder().encodeToString(iv);
    }
}
