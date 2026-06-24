package com.security.backend.exception;

/**
 * 基础业务异常。
 */
public class BaseException extends RuntimeException {

    /**
     * 响应状态码。
     */
    private final int code;

    /**
     * 创建基础业务异常。
     *
     * @param code 响应状态码
     * @param message 异常消息
     */
    public BaseException(int code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * 创建基础业务异常。
     *
     * @param code 响应状态码
     * @param message 异常消息
     * @param cause 原始异常
     */
    public BaseException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    /**
     * 获取响应状态码。
     *
     * @return 响应状态码
     */
    public int getCode() {
        return code;
    }
}
