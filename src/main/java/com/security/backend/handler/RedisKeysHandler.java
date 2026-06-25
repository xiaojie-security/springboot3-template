package com.security.backend.handler;


import com.security.backend.enums.SysConfigKey;

public class RedisKeysHandler {

    public static String getUserPermission(Long userId) {
        return "auth:perms:" + userId;
    }

    public static String getDeviceToken(Long userId, String deviceId) {
        return "login:device:" + userId + ":" + deviceId;
    }

    public static String getUserDevices(Long userId) {
        return "login:devices:" + userId;
    }

    public static String getLoginHistory(Long userId) {
        return "login:history:" + userId;
    }
    public static String getSystemConfig(String configKey) {
        return "system:config:" + configKey;
    }
    public static String getSystemConfig(SysConfigKey config) {
        return "system:config:" + config.name();
    }

    public static String getSecurityNonce(String nonce) {
        return "security:nonce:" + nonce;
    }

}
