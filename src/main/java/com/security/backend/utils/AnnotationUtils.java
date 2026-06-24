package com.security.backend.utils;

import cn.hutool.core.annotation.AnnotationUtil;
import org.springframework.core.MethodParameter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class AnnotationUtils extends AnnotationUtil {

    /**
     * 检查方法或类上是否标注了 指定注解。
     *
     * @param returnType 返回值类型
     * @return true 表示需要忽略加密处理
     */
    public static boolean hasIgnoreAnnotation(MethodParameter returnType, Class<? extends Annotation> annotationClass ) {
        return hasIgnoreMethodAnnotation(returnType, annotationClass) || hasIgnoreClassAnnotation(returnType, annotationClass);
    }

    /**
     * 检查方法上是否标注了 指定注解。
     *
     * @param returnType 返回值类型
     * @return true 表示需要忽略加密处理
     */
    public static boolean hasIgnoreMethodAnnotation(MethodParameter returnType, Class<? extends Annotation> annotationClass ) {
        Method method = returnType.getMethod();
        return method != null && method.isAnnotationPresent(annotationClass);
    }

    /**
     * 检查类上是否标注了 指定注解。
     *
     * @param returnType 返回值类型
     * @return true 表示需要忽略加密处理
     */
    public static boolean hasIgnoreClassAnnotation(MethodParameter returnType, Class<? extends Annotation> annotationClass ) {
        Class<?> declaringClass = returnType.getDeclaringClass();
        return declaringClass.isAnnotationPresent(annotationClass);
    }
}
