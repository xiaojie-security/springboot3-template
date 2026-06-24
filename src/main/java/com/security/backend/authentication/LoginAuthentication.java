package com.security.backend.authentication;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginAuthentication {
    /**
     * 访问令牌，用于用户身份验证和API访问授权
     */
    private String accessToken;

    /**
     * 刷新令牌，用于获取新的访问令牌
     */
    private String refreshToken;

}
