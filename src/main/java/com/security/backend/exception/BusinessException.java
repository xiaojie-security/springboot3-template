package com.security.backend.exception;

/**
 * 通用业务异常。
 */
public class BusinessException extends BaseException {

    /**
     * 默认业务错误码。
     */
    private static final int DEFAULT_ERROR_CODE = 500;

    /**
     * 操作失败。
     */
    public static final BusinessException OPERATION_FAILED = new BusinessException("操作失败");

    /**
     * 数据不存在。
     */
    public static final BusinessException DATA_NOT_FOUND = new BusinessException(404, "数据不存在");

    /**
     * 数据状态非法。
     */
    public static final BusinessException DATA_STATUS_INVALID = new BusinessException("数据状态非法");

    /**
     * 创建通用业务异常。
     *
     * @param message 异常消息
     */
    public BusinessException(String message) {
        super(DEFAULT_ERROR_CODE, message);
    }

    /**
     * 创建通用业务异常。
     *
     * @param code 响应状态码
     * @param message 异常消息
     */
    public BusinessException(int code, String message) {
        super(code, message);
    }

    /**
     * 创建通用业务异常。
     *
     * @param code 响应状态码
     * @param message 异常消息
     * @param cause 原始异常
     */
    public BusinessException(int code, String message, Throwable cause) {
        super(code, message, cause);
    }
}
