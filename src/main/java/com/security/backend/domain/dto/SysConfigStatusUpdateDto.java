package com.security.backend.domain.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 系统配置状态更新入参。
 */
@Data
public class SysConfigStatusUpdateDto {

    /**
     * 配置ID。
     */
    @NotNull(message = "配置ID不能为空")
    private Long configId;
}
