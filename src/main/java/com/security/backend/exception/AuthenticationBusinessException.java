package com.security.backend.exception;

/**
 * 认证异常。
 */
public class AuthenticationBusinessException extends BaseException {

    /**
     * 未登录。
     */
    public static final AuthenticationBusinessException UNAUTHORIZED =
            new AuthenticationBusinessException("请先登录");

    /**
     * 登录状态失效。
     */
    public static final AuthenticationBusinessException TOKEN_INVALID =
            new AuthenticationBusinessException("登录状态失效，请重新登录");

    /**
     * 认证方式异常
     */
    public static final AuthenticationBusinessException AUTHENTICATION_METHOD_ABNORMAL =
            new AuthenticationBusinessException("认证方式异常");

    /**
     * 创建认证异常。
     *
     * @param message 异常消息
     */
    public AuthenticationBusinessException(String message) {
        super(401, message);
    }
}
