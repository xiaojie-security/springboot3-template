package com.security.backend.encrypt;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EncryptResult {

    /**
     * AES-GCM初始化向量。
     */
    private String iv;

    /**
     * AES-GCM加密后的数据。
     */
    private String data;
}
