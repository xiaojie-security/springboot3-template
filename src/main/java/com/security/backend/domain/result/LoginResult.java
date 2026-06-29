package com.security.backend.domain.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginResult {

    /**
     * 访问令牌
     */
    private String accessToken;


    /**
     * 刷新令牌
     */
    private String refreshToken;

}
