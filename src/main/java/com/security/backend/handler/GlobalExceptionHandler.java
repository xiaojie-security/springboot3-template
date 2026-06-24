package com.security.backend.handler;

import com.security.backend.annotation.IgnoreEncrypt;
import com.security.backend.annotation.IgnoreResult;
import com.security.backend.domain.result.Result;
import com.security.backend.exception.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * 全局异常处理器。
 */
@Slf4j
@RestControllerAdvice
@IgnoreResult
@IgnoreEncrypt
public class GlobalExceptionHandler {

    /**
     * 处理基础业务异常。
     *
     * @param exception 基础业务异常
     * @return 统一响应结果
     */
    @ExceptionHandler(BaseException.class)
    public Result handleBaseException(BaseException exception) {
        log.error("GlobalExceptionHandler.handleBaseException 业务异常, code={}, message={}",
                exception.getCode(),
                exception.getMessage(),
                exception);
        return Result.create(exception.getCode(), exception.getMessage());
    }

    /**
     * 处理非法参数异常。
     *
     * @param exception 非法参数异常
     * @return 统一响应结果
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public Result handleIllegalArgumentException(IllegalArgumentException exception) {
        log.error("GlobalExceptionHandler.handleIllegalArgumentException 参数异常, message={}",
                exception.getMessage(),
                exception);
        return Result.create(HttpStatus.BAD_REQUEST.value(), exception.getMessage());
    }

    /**
     * 处理请求体不可读异常。
     *
     * @param exception 请求体不可读异常
     * @return 统一响应结果
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Result handleHttpMessageNotReadableException(HttpMessageNotReadableException exception) {
        log.error("GlobalExceptionHandler.handleHttpMessageNotReadableException 请求体不可读, message={}",
                exception.getMessage(),
                exception);
        return Result.create(HttpStatus.BAD_REQUEST.value(), "请求体格式错误");
    }

    /**
     * 处理缺失请求参数异常。
     *
     * @param exception 缺失请求参数异常
     * @return 统一响应结果
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public Result handleMissingServletRequestParameterException(MissingServletRequestParameterException exception) {
        log.error("GlobalExceptionHandler.handleMissingServletRequestParameterException 缺少请求参数, parameterName={}",
                exception.getParameterName(),
                exception);
        return Result.create(HttpStatus.BAD_REQUEST.value(), "缺少请求参数：" + exception.getParameterName());
    }

    /**
     * 处理参数类型不匹配异常。
     *
     * @param exception 参数类型不匹配异常
     * @return 统一响应结果
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public Result handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException exception) {
        log.error("GlobalExceptionHandler.handleMethodArgumentTypeMismatchException 参数类型错误, parameterName={}",
                exception.getName(),
                exception);
        return Result.create(HttpStatus.BAD_REQUEST.value(), "参数类型错误：" + exception.getName());
    }

    /**
     * 处理请求方法不支持异常。
     *
     * @param exception 请求方法不支持异常
     * @return 统一响应结果
     */
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Result handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException exception) {
        log.error("GlobalExceptionHandler.handleHttpRequestMethodNotSupportedException 请求方法不支持, method={}",
                exception.getMethod(),
                exception);
        return Result.create(HttpStatus.METHOD_NOT_ALLOWED.value(), "请求方法不支持：" + exception.getMethod());
    }

    /**
     * 处理媒体类型不支持异常。
     *
     * @param exception 媒体类型不支持异常
     * @return 统一响应结果
     */
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public Result handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException exception) {
        log.error("GlobalExceptionHandler.handleHttpMediaTypeNotSupportedException 媒体类型不支持, contentType={}",
                exception.getContentType(),
                exception);
        return Result.create(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(), "请求媒体类型不支持");
    }

    /**
     * 处理系统异常。
     *
     * @param exception 系统异常
     * @return 统一响应结果
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public Result handleException(Exception exception) {
        log.error("GlobalExceptionHandler.handleException 系统异常, message={}",
                exception.getMessage(),
                exception);
        return Result.create(HttpStatus.INTERNAL_SERVER_ERROR.value(), "系统异常，请稍后再试");
    }
}
