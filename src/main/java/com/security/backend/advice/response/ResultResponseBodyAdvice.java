package com.security.backend.advice.response;

import cn.hutool.json.JSONUtil;
import com.security.backend.annotation.IgnoreEncrypt;
import com.security.backend.annotation.IgnoreResult;
import com.security.backend.domain.result.Result;
import com.security.backend.encrypt.EncryptResult;
import com.security.backend.utils.AnnotationUtils;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.core.MethodParameter;
import org.springframework.core.Ordered;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.lang.reflect.Method;

/**
 * 统一响应拦截器
 * 将Controller返回的数据包装成标准RESTful格式
 */
@RestControllerAdvice
@RequiredArgsConstructor
public class ResultResponseBodyAdvice implements ResponseBodyAdvice<Object>, Ordered {

    @Override
    public int getOrder() {
        return 1;
    }

    /**
     * 是否需要处理
     * 返回true表示需要包装，false表示不需要
     */
    @Override
    public boolean supports(@NonNull MethodParameter returnType, @NonNull Class<? extends HttpMessageConverter<?>> converterType) {
        return !AnnotationUtils.hasIgnoreAnnotation(returnType, IgnoreResult.class);
    }

    /**
     * 包装响应数据
     */
    @Override
    public Object beforeBodyWrite(Object body,
                                  @NonNull MethodParameter returnType,
                                  @NonNull MediaType selectedContentType,
                                  @NonNull Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  @NonNull ServerHttpRequest request,
                                  @NonNull ServerHttpResponse response) {

        if (body instanceof Result) {
            return body;
        }
        if (body instanceof EncryptResult encryptResult) {
            return Result.success(encryptResult.getData(), encryptResult.getIv());
        }

        if (body instanceof String) {
            return JSONUtil.toJsonStr(Result.success(body));
        }
        return Result.success(body);
    }
}
