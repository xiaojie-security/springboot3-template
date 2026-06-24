package com.security.backend.controller;

import com.security.backend.domain.dto.SysConfigStatusUpdateDto;
import com.security.backend.domain.dto.SysConfigValueUpdateDto;
import com.security.backend.domain.vo.SysConfigVo;
import com.security.backend.service.SysConfigService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 系统配置接口。
 */
@RestController
@Validated
@RequestMapping("/sys-config")
@RequiredArgsConstructor
public class SysConfigController {

    private final SysConfigService sysConfigService;

    /**
     * 获取系统配置列表。
     *
     * @return 系统配置列表
     */
    @GetMapping("/list")
    public List<SysConfigVo> queryConfigList() {
        return sysConfigService.queryConfigList();
    }

    /**
     * 修改配置值。
     *
     * @param dto 修改配置值入参
     */
    @PostMapping("/update/value")
    public void updateConfigValue(@Valid @RequestBody SysConfigValueUpdateDto dto) {
        sysConfigService.updateConfigValue(dto.getConfigId(), dto.getConfigValue());
    }

    /**
     * 修改配置状态。
     *
     * @param dto 修改配置状态入参
     */
    @PostMapping("/update/status")
    public void updateConfigStatus(@Valid @RequestBody SysConfigStatusUpdateDto dto) {
        sysConfigService.updateConfigStatus(dto.getConfigId());
    }
}
