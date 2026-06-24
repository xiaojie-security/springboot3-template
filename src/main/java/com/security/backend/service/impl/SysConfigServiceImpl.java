package com.security.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.security.backend.domain.SysConfig;
import com.security.backend.domain.vo.SysConfigVo;
import com.security.backend.enums.Status;
import com.security.backend.exception.BusinessException;
import com.security.backend.handler.SysConfigHandler;
import com.security.backend.mapper.SysConfigMapper;
import com.security.backend.service.SysConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author 17607
* @description 针对表【sys_config(系统配置表)】的数据库操作Service实现
* @createDate 2026-06-23 09:55:19
*/
@Service
@Slf4j
@RequiredArgsConstructor
public class SysConfigServiceImpl extends ServiceImpl<SysConfigMapper, SysConfig>
    implements SysConfigService{

    private final SysConfigHandler sysConfigHandler;

    /**
     * 查询系统配置列表。
     *
     * @return 系统配置列表
     */
    @Override
    public List<SysConfigVo> queryConfigList() {
        List<SysConfig> configList = list(
                new LambdaQueryWrapper<SysConfig>()
                        .orderByAsc(SysConfig::getId)
        );
        return convertSysConfigVoList(configList);
    }

    @Override
    public List<SysConfig> listEnabled() {
        return list(
                new LambdaQueryWrapper<SysConfig>()
                        .eq(SysConfig::getStatus, Status.ENABLE)
        );
    }

    /**
     * 更新配置值。
     *
     * @param configId 配置ID
     * @param configValue 配置值
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateConfigValue(Long configId, String configValue) {
        SysConfig config = getById(configId);
        if (config == null) {
            log.error("SysConfigServiceImpl.updateConfigValue 配置不存在, configId={}", configId);
            throw BusinessException.DATA_NOT_FOUND;
        }

        boolean success = update(
                new LambdaUpdateWrapper<SysConfig>()
                        .eq(SysConfig::getId, configId)
                        .set(SysConfig::getConfigValue, configValue)
        );
        if (!success) {
            log.error("SysConfigServiceImpl.updateConfigValue 更新配置值失败, configId={}, configValue={}", configId, configValue);
            throw BusinessException.OPERATION_FAILED;
        }

        config.setConfigValue(configValue);
        sysConfigHandler.refreshConfigCache(config);
    }

    /**
     * 更新配置状态。
     *
     * @param configId 配置ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateConfigStatus(Long configId) {
        SysConfig config = getById(configId);
        if (config == null) {
            log.error("SysConfigServiceImpl.updateConfigStatus 配置不存在, configId={}", configId);
            throw BusinessException.DATA_NOT_FOUND;
        }

        Status targetStatus = Status.ENABLE == config.getStatus()
                ? Status.DISABLE
                : Status.ENABLE;
        boolean success = update(
                new LambdaUpdateWrapper<SysConfig>()
                        .eq(SysConfig::getId, configId)
                        .set(SysConfig::getStatus, targetStatus)
        );
        if (!success) {
            log.error("SysConfigServiceImpl.updateConfigStatus 更新配置状态失败, configId={}, targetStatus={}", configId, targetStatus);
            throw BusinessException.OPERATION_FAILED;
        }

        config.setStatus(targetStatus);
        sysConfigHandler.refreshConfigCache(config);
    }

    /**
     * 实体转换为VO。
     *
     * @param config 系统配置实体
     * @return 系统配置VO
     */
    private SysConfigVo convertSysConfigVo(SysConfig config) {
        if (config == null) {
            return null;
        }

        SysConfigVo vo = new SysConfigVo();
        vo.setId(config.getId());
        vo.setConfigKey(config.getConfigKey());
        vo.setConfigValue(config.getConfigValue());
        vo.setConfigType(config.getConfigType());
        vo.setConfigName(config.getConfigName());
        vo.setRemark(config.getRemark());
        vo.setStatus(config.getStatus().getCode());
        vo.setCreateTime(config.getCreateTime());
        vo.setUpdateTime(config.getUpdateTime());
        return vo;
    }

    /**
     * 实体集合转换为VO集合。
     *
     * @param configList 系统配置实体集合
     * @return 系统配置VO集合
     */
    private List<SysConfigVo> convertSysConfigVoList(List<SysConfig> configList) {
        if (configList == null || configList.isEmpty()) {
            return Collections.emptyList();
        }

        return configList.stream()
                .map(this::convertSysConfigVo)
                .collect(Collectors.toList());
    }
}




