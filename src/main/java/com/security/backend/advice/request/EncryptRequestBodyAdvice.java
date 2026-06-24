package com.security.backend.advice.request;

import cn.hutool.json.JSONUtil;
import com.security.backend.annotation.IgnoreEncrypt;
import com.security.backend.context.ContextHolder;
import com.security.backend.encrypt.EncryptBody;
import com.security.backend.encrypt.EncryptService;
import com.security.backend.exception.BadRequestException;
import com.security.backend.handler.SysConfigHandler;
import com.security.backend.utils.AnnotationUtils;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.core.MethodParameter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Objects;


/**
 * 加密请求拦截器。
 */
@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class EncryptRequestBodyAdvice implements RequestBodyAdvice, Ordered {

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
     * 判断是否支持请求解密。
     *
     * @param methodParameter 方法参数
     * @param targetType 目标类型
     * @param converterType 消息转换器类型
     * @return 是否支持
     */
    @Override
    public boolean supports(@NonNull MethodParameter methodParameter, @NonNull Type targetType, @NonNull Class<? extends HttpMessageConverter<?>> converterType) {
        if (!sysConfigHandler.enableDataTransferEncrypt()) {
            return false;
        }

        if (AnnotationUtils.hasIgnoreAnnotation(methodParameter,IgnoreEncrypt.class)) {
            log.debug("EncryptRequestBodyAdvice.supports 方法或类标注了 @IgnoreEncrypt，跳过解密处理, method={}",
                    Objects.requireNonNull(methodParameter.getMethod()).getName());
            return false;
        }
        return true;
    }

    /**
     * 读取并解密请求体。
     *
     * @param inputMessage 请求消息
     * @param parameter 方法参数
     * @param targetType 目标类型
     * @param converterType 消息转换器类型
     * @return 解密后的请求消息
     * @throws IOException IO异常
     */
    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) throws IOException {
        String body = new String(inputMessage.getBody().readAllBytes(), StandardCharsets.UTF_8);
        if (!StringUtils.hasText(body)) {
            log.error("EncryptRequestInterceptor.beforeBodyRead 请求体为空");
            throw BadRequestException.BODY_EMPTY;
        }
        EncryptBody encryptBody = JSONUtil.toBean(body, EncryptBody.class);
        if (!isEncryptBody(encryptBody)) {
            log.error("EncryptRequestInterceptor.beforeBodyRead 加密请求体格式错误");
            throw BadRequestException.BODY_FORMAT_INVALID;
        }
        String aesSecret = encryptService.decryptWithPrivateKey(encryptBody.getSecret());
        ContextHolder.getEncryptContext().setAesSecret(aesSecret);
        String plainText = encryptService.decryptWithAes(encryptBody.getData(), aesSecret, encryptBody.getIv());
        return new DecryptHttpInputMessage(inputMessage.getHeaders(), plainText);
    }

    /**
     * 判断是否为完整加密请求体。
     *
     * @param encryptBody 加密请求体
     * @return 是否完整
     */
    private boolean isEncryptBody(EncryptBody encryptBody) {
        return encryptBody != null
                && StringUtils.hasText(encryptBody.getSecret())
                && StringUtils.hasText(encryptBody.getIv())
                && StringUtils.hasText(encryptBody.getData());
    }

    /**
     * 请求体读取后处理。
     *
     * @param body 请求体对象
     * @param inputMessage 请求消息
     * @param parameter 方法参数
     * @param targetType 目标类型
     * @param converterType 消息转换器类型
     * @return 请求体对象
     */
    @Override
    public Object afterBodyRead(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        return body;
    }

    /**
     * 空请求体处理。
     *
     * @param body 请求体对象
     * @param inputMessage 请求消息
     * @param parameter 方法参数
     * @param targetType 目标类型
     * @param converterType 消息转换器类型
     * @return 请求体对象
     */
    @Override
    public @Nullable Object handleEmptyBody(@Nullable Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        return body;
    }

    /**
     * 解密后的HTTP输入消息。
     */
    private static class DecryptHttpInputMessage implements HttpInputMessage {

        /**
         * 请求头。
         */
        private final HttpHeaders headers;

        /**
         * 解密后的请求体字节。
         */
        private final byte[] bodyBytes;

        /**
         * 创建解密后的HTTP输入消息。
         *
         * @param headers 请求头
         * @param body 解密后的请求体
         */
        private DecryptHttpInputMessage(HttpHeaders headers, String body) {
            this.headers = headers;
            this.bodyBytes = body.getBytes(StandardCharsets.UTF_8);
        }

        /**
         * 获取请求体输入流。
         *
         * @return 请求体输入流
         */
        @Override
        public InputStream getBody() {
            return new ByteArrayInputStream(bodyBytes);
        }

        /**
         * 获取请求头。
         *
         * @return 请求头
         */
        @Override
        public HttpHeaders getHeaders() {
            return headers;
        }
    }
}
