package com.security.backend.exception;

/**
 * 系统异常。
 */
public class SystemException extends BaseException {

    /**
     * 系统繁忙。
     */
    public static final SystemException SYSTEM_BUSY = new SystemException("系统繁忙，请稍后再试");

    /**
     * 系统错误。
     */
    public static final SystemException SYSTEM_ERROR = new SystemException("系统错误");

    /**
     * 创建系统异常。
     *
     * @param message 异常消息
     */
    public SystemException(String message) {
        super(500, message);
    }

    /**
     * 创建系统异常。
     *
     * @param message 异常消息
     * @param cause 原始异常
     */
    public SystemException(String message, Throwable cause) {
        super(500, message, cause);
    }
}
