package com.security.backend.controller;

import com.security.backend.annotation.IgnoreEncrypt;
import com.security.backend.annotation.OperationLog;
import com.security.backend.encrypt.EncryptService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 混合加密接口。
 */
@RestController
@RequestMapping("/encrypt")
@RequiredArgsConstructor
public class EncryptController {

    /**
     * 加密服务。
     */
    private final EncryptService encryptService;

    /**
     * 查询RSA公钥。
     *
     * @return RSA公钥
     */
    @IgnoreEncrypt
    @GetMapping("/public-key")
    @OperationLog
    public String queryPublicKey() {
        return encryptService.getPublicKey();
    }
}
