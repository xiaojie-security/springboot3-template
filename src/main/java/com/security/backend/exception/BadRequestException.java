package com.security.backend.exception;

/**
 * 请求参数异常。
 */
public class BadRequestException extends BaseException {

    /**
     * 请求参数错误。
     */
    public static final BadRequestException PARAM_INVALID = new BadRequestException("请求参数错误");

    /**
     * 请求体为空。
     */
    public static final BadRequestException BODY_EMPTY = new BadRequestException("请求体为空");

    /**
     * 请求体格式错误。
     */
    public static final BadRequestException BODY_FORMAT_INVALID = new BadRequestException("请求体格式错误");

    /**
     * 创建请求参数异常。
     *
     * @param message 异常消息
     */
    public BadRequestException(String message) {
        super(400, message);
    }

    /**
     * 创建请求参数异常。
     *
     * @param message 异常消息
     * @param cause 原始异常
     */
    public BadRequestException(String message, Throwable cause) {
        super(400, message, cause);
    }
}
