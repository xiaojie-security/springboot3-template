package com.security.backend.authentication;



import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.json.JSONUtil;
import com.security.backend.context.ContextHolder;
import com.security.backend.domain.DeviceLoginCredential;
import com.security.backend.domain.UserPrincipal;
import com.security.backend.domain.result.LoginResult;
import com.security.backend.domain.result.Result;
import com.security.backend.exception.AuthenticationBusinessException;
import com.security.backend.handler.JwtTokenHandler;
import com.security.backend.handler.RedisKeysHandler;
import com.security.backend.handler.SysConfigHandler;
import com.security.backend.config.properties.TokenProperties;
import com.security.backend.utils.HttpServletUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler{
    private final TokenProperties tokenProperties;
    private final SysConfigHandler sysConfigHandler;
    private final StringRedisTemplate redisTemplate;
    private final JwtTokenHandler jwtTokenHandler;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        String username = principal.getUsername();
        Long userId = principal.getUserId();
        log.info("AuthenticationSuccessHandlerImpl.onAuthenticationSuccess 认证成功 请求路径={} 用户名={}",request.getRequestURI(), username);
        // 1. 生成token
        String accessToken = jwtTokenHandler.createAccessToken(username, userId, tokenProperties.getAccess().getSecret());
        String refreshToken = jwtTokenHandler.createRefreshToken(tokenProperties.getRefresh().getSecret());

        // 2. 得到设备ID
        String deviceId = ContextHolder.getRequestContext().getDeviceId();

        // 3. 判断登录策略
        boolean singleLogin = sysConfigHandler.enableSingleLogin();

        // 4. 处理登录策略
        if (singleLogin){
            handleSsoLogin(userId, deviceId, accessToken, refreshToken);
        } else {
            handleMultiLogin(userId, deviceId, accessToken, refreshToken);
        }

        // 6. 构建返回结果
        LoginResult result = LoginResult.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

        // 7. 返回响应
        new HttpServletUtils(response).writeResponseBody(Result.success(result));
    }

    private void handleSsoLogin(Long userId, String deviceId, String accessToken, String refreshToken) {
        String userDevicesKey = RedisKeysHandler.getUserDevices(userId);

        // 1. 获取该用户所有在线设备
        Set<String> oldDeviceIds = redisTemplate.opsForSet().members(userDevicesKey);

        // 2. 删除所有旧设备的token
        if (CollUtil.isNotEmpty(oldDeviceIds)) {
            for (String oldDeviceId : oldDeviceIds) {
                String deviceTokenKey = RedisKeysHandler.getDeviceToken(userId, oldDeviceId);
                redisTemplate.delete(deviceTokenKey);
                log.info("CustomAuthenticationSuccessHandler.handleSsoLogin 单点登录踢掉设备: userId={}, deviceId={}", userId, oldDeviceId);
            }
        }
        // 3. 删除旧的用户token映射
        redisTemplate.delete(userDevicesKey);

        // 4. 保存新设备的token
        saveDeviceToken(userId, deviceId, accessToken, refreshToken);

        // 5. 记录该用户的所有设备（只保留当前设备）
        redisTemplate.opsForSet().add(userDevicesKey, deviceId);
        redisTemplate.expire(userDevicesKey, sysConfigHandler.queryTokenExpireSeconds(), TimeUnit.SECONDS);

        log.info("CustomAuthenticationSuccessHandler.handleSsoLogin 单点登录成功: userId={}, deviceId={}", userId, deviceId);
    }

    private void handleMultiLogin(Long userId, String deviceId, String accessToken, String refreshToken) {
        String userDevicesKey = RedisKeysHandler.getUserDevices(userId);

        // 1. 检查该设备是否已登录（如果已登录，更新token而不是新增）
        String deviceTokenKey = RedisKeysHandler.getDeviceToken(userId, deviceId);
        Boolean exists = redisTemplate.hasKey(deviceTokenKey);

        if (BooleanUtil.isTrue(exists)) {
            // 设备已存在，更新token（相当于刷新）
            log.info("CustomAuthenticationSuccessHandler.handleMultiLogin 设备已存在，更新token: userId={}, deviceId={}", userId, deviceId);
        } else {
            // 新设备，记录到设备列表
            redisTemplate.opsForSet().add(userDevicesKey, deviceId);
            log.info("CustomAuthenticationSuccessHandler.handleMultiLogin 新设备登录: userId={}, deviceId={}", userId, deviceId);
        }

        // 2. 保存新设备的token
        saveDeviceToken(userId, deviceId, accessToken, refreshToken);

        // 3. 设置设备列表过期时间
        redisTemplate.expire(userDevicesKey, sysConfigHandler.queryTokenExpireSeconds(), TimeUnit.SECONDS);

        log.info("CustomAuthenticationSuccessHandler.handleMultiLogin 多点登录成功: userId={}, deviceId={}", userId, deviceId);
    }

    /**
     * 保存设备token
     */
    private void saveDeviceToken(Long userId, String deviceId, String accessToken, String refreshToken) {
        String deviceTokenKey = RedisKeysHandler.getDeviceToken(userId, deviceId);

        Long expireSeconds = sysConfigHandler.queryTokenExpireSeconds();

        // 存储完整的登录信息
        DeviceLoginCredential loginInfo = DeviceLoginCredential.builder()
                .userId(userId)
                .deviceId(deviceId)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .loginTime(LocalDateTime.now().toString())
                .expireTime(LocalDateTime.now().plusSeconds(expireSeconds).toString())
                .build();

        try {
            String json = JSONUtil.toJsonStr(loginInfo);
            redisTemplate.opsForValue().set(deviceTokenKey, json, expireSeconds, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("CustomAuthenticationSuccessHandler.saveDeviceToken 保存设备token失败", e);
            throw new AuthenticationBusinessException("保存登录信息失败");
        }
    }

}
