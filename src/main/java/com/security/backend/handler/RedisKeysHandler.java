package com.security.backend.handler;


import com.security.backend.enums.SysConfigKey;

public class RedisKeysHandler {

    public static String getUserPermission(Long userId) {
        return "auth:perms:" + userId;
    }

    public static String getAccessToken() {
        return "auth:access:token";
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
