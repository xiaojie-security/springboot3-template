package com.security.backend.utils;

import cn.hutool.json.JSONUtil;
import com.security.backend.domain.result.Result;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;


import java.io.BufferedInputStream;
import java.io.IOException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StreamUtils;
import org.springframework.web.util.ContentCachingRequestWrapper;


import java.nio.charset.StandardCharsets;

/**
 * HTTP 请求/响应工具类。
 * <p>
 * 提供读取请求体、写入响应体的便捷方法，支持 JSON 格式数据的序列化与反序列化。
 * </p>
 *
 * @author system
 * @since 1.0.0
 */
@Slf4j
public class HttpUtils {

    /**
     * 响应内容类型：JSON。
     */
    public static final String CONTENT_TYPE = "application/json";

    /**
     * 字符集编码：UTF-8。
     */
    public static final String CHARSET = "utf-8";

    /**
     * 从 HttpServletRequest 中读取请求体，并反序列化为指定类型的对象。
     *
     * @param request HttpServletRequest 对象
     * @param tClass  目标类型 Class
     * @param <T>     泛型类型
     * @return 反序列化后的对象，如果请求体为空则返回 null
     * @throws IOException 读取请求体或反序列化失败时抛出
     */
    public static <T> T readRequestBody(HttpServletRequest request, Class<T> tClass) throws IOException {
        String body = readRequestBody(request);
        if (body == null || body.isEmpty()) {
            return null;
        }
        return JSONUtil.toBean(body, tClass);
    }

    /**
     * 从 HttpServletRequest 中读取完整的请求体字符串。
     * <p>
     * 该方法通过 {@link HttpServletRequest#getInputStream()} 获取请求体流，
     * 并以 UTF-8 编码解析为字符串。
     * </p>
     * <p>
     * <b>注意：</b>请求体流只能读取一次，多次调用将返回空字符串。
     * 如需在拦截器或切面中重复读取，需配合 {@link ContentCachingRequestWrapper} 使用。
     * </p>
     *
     * @param request HttpServletRequest 对象
     * @return 请求体字符串，若流为空则返回空字符串
     * @throws IOException 读取请求体失败时抛出
     */
    public static String readRequestBody(HttpServletRequest request) throws IOException {
        // 获取请求体输入流
        ServletInputStream inputStream = request.getInputStream();
        // 使用缓冲流提高读取性能
        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
        // 构建字符串
        StringBuilder builder = new StringBuilder();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = bufferedInputStream.read(buffer)) != -1) {
            builder.append(new String(buffer, 0, length, StandardCharsets.UTF_8));
        }
        return builder.toString();
    }

    /**
     * 使用 Spring 的 {@link StreamUtils} 读取请求体（推荐方式）。
     * <p>
     * 此方法更简洁，且内部处理了流关闭等细节，建议替代手动读取的方式。
     * </p>
     *
     * @param request HttpServletRequest 对象
     * @return 请求体字符串
     * @throws IOException 读取请求体失败时抛出
     */
    public static String readRequestBodyWithStreamUtils(HttpServletRequest request) throws IOException {
        return StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
    }

    /**
     * 向 HttpServletResponse 写入响应体（使用 HttpStatus）。
     * <p>
     * 自动将 HttpStatus 转换为对应的状态码和描述信息，构建统一的 Result 对象返回。
     * </p>
     *
     * @param response   HttpServletResponse 对象
     * @param httpStatus HTTP 状态枚举
     */
    public static void writeResponseBody(HttpServletResponse response, HttpStatus httpStatus) {
        writeResponseBody(response, Result.create(httpStatus.value(), httpStatus.getReasonPhrase()));
    }

    /**
     * 向 HttpServletResponse 写入响应体（自定义状态码和消息）。
     *
     * @param response HttpServletResponse 对象
     * @param code     自定义状态码
     * @param message  响应消息
     */
    public static void writeResponseBody(HttpServletResponse response, int code, String message) {
        writeResponseBody(response, Result.create(code, message));
    }

    /**
     * 向 HttpServletResponse 写入响应体（使用 Result 对象）。
     * <p>
     * 该方法会将 Result 对象序列化为 JSON 字符串，设置响应头为 application/json，
     * 并写入响应输出流。
     * </p>
     *
     * @param response HttpServletResponse 对象
     * @param result   响应结果对象
     * @throws RuntimeException 写入响应失败时抛出（包装 IOException）
     */
    public static void writeResponseBody(HttpServletResponse response, Result result) {
        // 设置响应内容类型为 JSON
        response.setContentType(CONTENT_TYPE);
        // 设置响应状态码（注意：http 状态码通常是 200，业务状态码放在 Result 中）
        response.setStatus(HttpStatus.OK.value());
        // 设置字符编码
        response.setCharacterEncoding(CHARSET);
        try {
            // 将 Result 对象序列化为 JSON 并写入响应流
            response.getWriter().write(JSONUtil.toJsonStr(result));
            response.getWriter().flush();
        } catch (IOException e) {
            log.error("HttpUtils.writeResponseBody 写入响应失败", e);
            throw new RuntimeException("写入响应失败", e);
        }
    }

    /**
     * 向 HttpServletResponse 写入响应体（使用 Result 对象，可自定义 HTTP 状态码）。
     * <p>
     * 与 {@link #writeResponseBody(HttpServletResponse, Result)} 的区别在于，
     * 此方法允许指定 HTTP 协议级别的状态码。
     * </p>
     *
     * @param response     HttpServletResponse 对象
     * @param result       响应结果对象
     * @param httpStatus   HTTP 状态码（如 200、401、500 等）
     */
    public static void writeResponseBody(HttpServletResponse response, Result result, int httpStatus) {
        response.setContentType(CONTENT_TYPE);
        response.setStatus(httpStatus);
        response.setCharacterEncoding(CHARSET);
        try {
            response.getWriter().write(JSONUtil.toJsonStr(result));
            response.getWriter().flush();
        } catch (IOException e) {
            log.error("HttpUtils.writeResponseBody 写入响应失败", e);
            throw new RuntimeException("写入响应失败", e);
        }
    }
}
