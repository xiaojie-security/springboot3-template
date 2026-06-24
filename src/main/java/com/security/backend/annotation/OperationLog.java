package com.security.backend.annotation;

import com.security.backend.enums.OperationModule;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 操作日志注解
 * 用于标记需要记录操作日志的方法
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OperationLog {
    
    /**
     * 操作模块
     */
    OperationModule module() default  OperationModule.DEFAULT;
    
    /**
     * 操作描述
     */
    String description() default "";

    /**
     * 是否保存请求参数
     */
    boolean saveRequestParams() default true;

    /**
     * 是否保存响应体
     */
    boolean saveResponseBody() default true;
}
