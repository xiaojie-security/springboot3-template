package com.security.backend.domain.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LoginDto {

    /**
     * 登录主体
     */
    @NotNull(message = "登录主体不可为空")
    private String principal;

    /**
     * 登录凭证
     */
    @NotNull(message = "登录凭证不可为空")
    private String credentials;
}
