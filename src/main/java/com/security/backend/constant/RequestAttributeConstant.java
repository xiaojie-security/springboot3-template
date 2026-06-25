package com.security.backend.constant;

/**
 * 请求属性常量类
 * 用于存储请求上下文中的属性键名
 */
public final class RequestAttributeConstant {

    private RequestAttributeConstant() {
        // 私有构造方法，防止实例化
    }

    // ========== 用户相关 ==========

    /**
     * 用户名
     */
    public static final String USERNAME = "username";

    /**
     * 用户ID
     */
    public static final String USER_ID = "userId";

    /**
     * 访问令牌
     */
    public static final String ACCESS_TOKEN = "accessToken";

    /**
     * 刷新令牌
     */
    public static final String REFRESH_TOKEN = "refreshToken";

    // ========== 设备相关 ==========

    /**
     * 设备ID
     */
    public static final String DEVICE_ID = "deviceId";

    /**
     * 设备类型 (WEB, APP, H5, PC, MOBILE等)
     */
    public static final String DEVICE_TYPE = "deviceType";

    /**
     * 设备名称
     */
    public static final String DEVICE_NAME = "deviceName";

    /**
     * 设备型号
     */
    public static final String DEVICE_MODEL = "deviceModel";

    // ========== 网络相关 ==========

    /**
     * IPv4地址
     */
    public static final String IPV4 = "ipv4";

    /**
     * IPv6地址
     */
    public static final String IPV6 = "ipv6";


    // ========== 请求相关 ==========

    /**
     * User-Agent
     */
    public static final String USER_AGENT = "userAgent";

}
