package com.security.backend.advice.response;

import cn.hutool.json.JSONUtil;
import com.security.backend.annotation.IgnoreEncrypt;
import com.security.backend.context.ContextHolder;
import com.security.backend.context.EncryptContext;
import com.security.backend.encrypt.EncryptService;
import com.security.backend.handler.SysConfigHandler;
import com.security.backend.utils.AnnotationUtils;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.core.MethodParameter;
import org.springframework.core.Ordered;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * 加密响应拦截器。
 */
@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class EncryptResponseBodyAdvice implements ResponseBodyAdvice<Object>, Ordered {

    /**
     * 加密服务。
     */
    private final EncryptService encryptService;

    @Resource
    private SysConfigHandler sysConfigHandler;

    /**
     * 获取拦截器顺序。
     *
     * @return 顺序值
     */
    @Override
    public int getOrder() {
        return 0;
    }

    /**
     * 判断是否支持响应加密。
     *
     * 校验条件：
     * 1. 系统配置开启了数据传输加密
     * 2. 控制器方法或类上没有标注 @IgnoreEncrypt 注解
     *
     * @param returnType 返回值类型
     * @param converterType 消息转换器类型
     * @return 是否支持
     */
    @Override
    public boolean supports(@NonNull MethodParameter returnType,
                            @NonNull Class<? extends HttpMessageConverter<?>> converterType) {

        // 1. 检查系统配置是否启用加密
        if (!sysConfigHandler.enableDataTransferEncrypt()) {
            return false;
        }

        // 2. 检查控制器方法或类是否标注了 @IgnoreEncrypt 注解
        if (AnnotationUtils.hasIgnoreAnnotation(returnType, IgnoreEncrypt.class)) {
            log.debug("EncryptResponseBodyAdvice.supports 方法或类标注了 @IgnoreEncrypt 跳过加密处理, method={}",
                    returnType.getMethod() != null ? returnType.getMethod().getName() : "unknown");
            return false;
        }
        return true;
    }

    /**
     * 响应写出前加密。
     *
     * @param body 响应体
     * @param returnType 返回值类型
     * @param selectedContentType 响应内容类型
     * @param selectedConverterType 消息转换器类型
     * @param request 请求对象
     * @param response 响应对象
     * @return 加密后的响应体
     */
    @Override
    public @Nullable Object beforeBodyWrite(@Nullable Object body, @NonNull MethodParameter returnType,
                                            @NonNull MediaType selectedContentType, @NonNull Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                            @NonNull ServerHttpRequest request, @NonNull ServerHttpResponse response) {
        EncryptContext encryptContext = ContextHolder.getEncryptContext();
        String aesSecret = encryptContext.getAesSecret();
        if (!StringUtils.hasText(aesSecret)) {
            log.debug("EncryptResponseInterceptor.beforeBodyWrite AES密钥为空 跳过响应加密");
            return body;
        }
        String plainText = body instanceof String ? (String) body : JSONUtil.toJsonStr(body);
        return encryptService.encryptWithAesByEncryptResult(plainText, aesSecret);
    }


}
