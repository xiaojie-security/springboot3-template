package com.security.backend.exception;

/**
 * 权限异常。
 */
public class ForbiddenBusinessException extends BaseException {

    /**
     * 无访问权限。
     */
    public static final ForbiddenBusinessException ACCESS_DENIED =
            new ForbiddenBusinessException("无权限访问");

    /**
     * 创建权限异常。
     *
     * @param message 异常消息
     */
    public ForbiddenBusinessException(String message) {
        super(403, message);
    }
}
