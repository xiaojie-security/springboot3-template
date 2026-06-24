package com.security.backend.service;

import com.security.backend.domain.SysConfig;
import com.security.backend.domain.vo.SysConfigVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author 17607
* @description 针对表【sys_config(系统配置表)】的数据库操作Service
* @createDate 2026-06-23 09:55:19
*/
public interface SysConfigService extends IService<SysConfig> {

    /**
     * 查询系统配置列表。
     *
     * @return 系统配置列表
     */
    List<SysConfigVo> queryConfigList();

    /**
     * 获取所有可用配置
     * @return 可用配置列表
     */
    List<SysConfig> listEnabled();

    /**
     * 更新配置值。
     *
     * @param configId 配置ID
     * @param configValue 配置值
     */
    void updateConfigValue(Long configId, String configValue);

    /**
     * 更新配置状态。
     *
     * @param configId 配置ID
     */
    void updateConfigStatus(Long configId);
}
