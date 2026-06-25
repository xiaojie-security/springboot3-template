package com.security.backend.handler;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.security.backend.cache.CacheService;
import com.security.backend.domain.SysConfig;
import com.security.backend.enums.Status;
import com.security.backend.enums.SysConfigKey;
import com.security.backend.service.SysConfigService;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 系统配置处理器。
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class SysConfigHandler {

    @Resource
    @Lazy
    private CacheService cacheService;

    @Resource
    @Lazy
    private SysConfigService sysConfigService;


    /**
     * 开启防重放攻击
     * @return 是否开启
     */
    public boolean enableReplayAttack() {
        SysConfig config = queryConfig(SysConfigKey.REPLAY_ATTACK_ENABLED);
        return ObjUtil.isNotEmpty(config) && BooleanUtil.toBoolean(config.getConfigValue());
    }

    /**
     * 查询随机串防重过期时间。
     *
     * @return 过期秒数
     */
    public Long queryNonceExpireSeconds() {
        if (!enableReplayAttack()){
            return null;
        }

        SysConfig timeoutConfig = queryConfig(SysConfigKey.NONCE_CACHE_EXPIRE);
        if (ObjUtil.isEmpty(timeoutConfig) || !NumberUtil.isLong(timeoutConfig.getConfigValue())) {
            return null;
        }
        return Long.parseLong(timeoutConfig.getConfigValue());
    }

    /**
     * 查询防重放超时时间。
     *
     * @return 超时秒数
     */
    public Long queryReplayAttackTimeoutSeconds() {
        if (!enableReplayAttack()){
            return null;
        }

        SysConfig timeoutConfig = queryConfig(SysConfigKey.REPLAY_ATTACK_TIMEOUT);
        if (ObjUtil.isEmpty(timeoutConfig) || !NumberUtil.isLong(timeoutConfig.getConfigValue())) {
            return null;
        }
        return Long.parseLong(timeoutConfig.getConfigValue());
    }

    /**
     * 是否启用数据传输加密。
     *
     * @return true-启用，false-禁用
     */
    public boolean enableDataTransferEncrypt() {
        SysConfig config = queryConfig(SysConfigKey.DATA_TRANSFER_ENCRYPT);
        return ObjUtil.isNotEmpty(config) && BooleanUtil.toBoolean(config.getConfigValue());
    }

    /**
     * 是否启用用户注册。
     *
     * @return true-启用，false-禁用
     */
    public boolean enableUserRegister() {
        SysConfig config = queryConfig(SysConfigKey.USER_REGISTER);
        return ObjUtil.isNotEmpty(config) && BooleanUtil.toBoolean(config.getConfigValue());
    }

    /**
     * 是否启用操作日志。
     *
     * @return true-启用，false-禁用
     */
    public boolean enableOperationLog() {
        SysConfig config = queryConfig(SysConfigKey.OPERATION_LOG);
        return ObjUtil.isNotEmpty(config) && BooleanUtil.toBoolean(config.getConfigValue());
    }

    /**
     * 是否启用单点登录限制。
     *
     * @return true-启用，false-禁用
     */
    public boolean enableSingleLogin() {
        SysConfig config = queryConfig(SysConfigKey.SINGLE_LOGIN);
        return ObjUtil.isNotEmpty(config) && BooleanUtil.toBoolean(config.getConfigValue());
    }

    /**
     * 查询令牌过期时长。
     *
     * @return 过期分钟数
     */
    public Long queryTokenExpireSeconds() {
        SysConfig config = queryConfig(SysConfigKey.TOKEN_EXPIRE_SECONDS);
        if (ObjUtil.isEmpty(config) || !NumberUtil.isLong(config.getConfigValue())) {
            return null;
        }
        return Long.parseLong(config.getConfigValue());
    }

    /**
     * 是否启用系统维护。
     *
     * @return true-启用，false-禁用
     */
    public boolean enableSystemMaintenance() {
        SysConfig config = queryConfig(SysConfigKey.SYSTEM_MAINTENANCE);
        return ObjUtil.isNotEmpty(config) && BooleanUtil.toBoolean(config.getConfigValue());
    }

    /**
     * 查询系统配置。
     *
     * @param config 系统配置枚举
     * @return 系统配置
     */
    public SysConfig queryConfig(SysConfigKey config) {
        String cacheKey = RedisKeysHandler.getSystemConfig(config);
        return cacheService.getOrLoad(
                cacheKey, SysConfig.class,
                () -> sysConfigService.getOne(
                        new LambdaQueryWrapper<SysConfig>()
                                .eq(SysConfig::getConfigKey, config.name())
                                .eq(SysConfig::getStatus, Status.ENABLE.getCode())
                                .last("limit 1")
                )
        );
    }

    /**
     * 加载系统配置到缓存
     */
    public void loadSystemConfigCache() {
        // 查询所有启用的配置
        List<SysConfig> configList = sysConfigService.listEnabled();

        for (SysConfig config : configList) {
            refreshConfigCache(config);
        }

        log.info("SysConfigHandler.loadSystemConfigCache 加载 {} 条配置到缓存", configList.size());
    }

    /**
     * 刷新系统配置缓存。
     *
     * @param config 系统配置
     */
    public void refreshConfigCache(SysConfig config) {
        String key = RedisKeysHandler.getSystemConfig(config.getConfigKey());
        if (config.getStatus() == Status.ENABLE) {
            cacheService.set(key, config);
            return;
        }
        cacheService.delete(key);
    }
}
