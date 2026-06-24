package com.security.backend.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 系统配置值更新入参。
 */
@Data
public class SysConfigValueUpdateDto {

    /**
     * 配置ID。
     */
    @NotNull(message = "配置ID不能为空")
    private Long configId;

    /**
     * 配置值。
     */
    @NotBlank(message = "配置值不能为空")
    private String configValue;
}
