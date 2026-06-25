package com.security.backend.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 设备登录凭证信息
 *
 * @author YourName
 * @date 2024-01-01
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceLoginCredential implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 设备唯一标识
     */
    private String deviceId;

    /**
     * 访问令牌
     */
    private String accessToken;

    /**
     * 刷新令牌
     */
    private String refreshToken;

    /**
     * 登录时间
     */
    private String loginTime;

    /**
     * 过期时间
     */
    private String expireTime;
}
