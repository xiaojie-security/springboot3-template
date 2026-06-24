package com.security.backend.annotation;

import java.lang.annotation.*;


@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface IgnoreEncrypt {

    /**
     * 是否忽略解密，默认为 true。
     */
    boolean value() default true;
}
