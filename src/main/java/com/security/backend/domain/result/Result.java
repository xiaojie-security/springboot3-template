package com.security.backend.domain.result;

import cn.hutool.json.JSONUtil;
import lombok.Data;

/**
 * 统一响应结果封装类。
 * <p>
 * 该类用于封装API接口的返回结果，包含状态码、消息、数据内容和AES加密初始化向量(IV)。
 * 采用不可变设计，所有字段均为final类型，确保线程安全。
 * </p>
 */
@Data
public final class Result {

    /**
     * 响应状态码
     */
    private final int code;

    /**
     * 响应消息
     */
    private final String msg;

    /**
     * 响应数据
     */
    private final Object data;

    /**
     * AES加密初始化向量（Initialization Vector）
     */
    private final String iv;

    /**
     * 私有构造方法 - 不带IV
     * <p>创建不含iv字段的Result实例，适用于数据未加密场景</p>
     *
     * @param code 状态码
     * @param msg 响应消息
     * @param data 响应数据（未加密或无需加密）
     */
    private Result(int code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
        this.iv = null;
    }

    /**
     * 私有构造方法 - 带IV
     * <p>创建包含iv字段的Result实例，用于响应数据需要AES加密的场景</p>
     *
     * @param code 状态码
     * @param msg 响应消息
     * @param data 响应数据（通常为加密后的数据）
     * @param iv AES加密初始化向量
     */
    private Result(int code, String msg, Object data, String iv) {
        this.code = code;
        this.msg = msg;
        this.data = data;
        this.iv = iv;
    }

    /**
     * 创建成功响应结果（无数据，无IV）
     * <p>状态码为200，消息为"Success"，数据和IV均为 {@code null}</p>
     *
     * @return 成功响应结果对象
     */
    public static Result success() {
        return new Result(200, "Success", null);
    }

    /**
     * 创建成功响应结果（携带数据，无IV）
     * <p>状态码为200，消息为"Success"，适用于数据未加密的响应场景</p>
     *
     * @param data 业务数据（明文）
     * @return 成功响应结果对象
     */
    public static Result success(Object data) {
        return new Result(200, "Success", data);
    }

    /**
     * 创建成功响应结果（携带加密数据和IV）
     * <p>状态码为200，消息为"Success"，包含IV用于客户端解密响应数据。</p>
     * <p>适用于需要对响应数据进行AES加密的安全传输场景</p>
     *
     * @param data 加密后的业务数据
     * @param iv AES加密初始化向量（Base64编码或十六进制字符串）
     * @return 成功响应结果对象
     */
    public static Result success(Object data, String iv) {
        return new Result(200, "Success", data, iv);
    }

    /**
     * 创建错误响应结果（携带IV）
     * <p>状态码为500，携带自定义错误消息和IV。</p>
     * <p>错误信息通常也需要加密传输，以防止敏感错误信息泄露</p>
     *
     * @param msg 错误消息（可能为加密后的密文）
     * @param iv AES加密初始化向量
     * @return 错误响应结果对象
     */
    public static Result error(String msg, String iv) {
        return new Result(500, msg, null, iv);
    }

    /**
     * 创建自定义响应结果（无数据和IV）
     * <p>适用于需要自定义状态码但无需返回数据和加密的场景</p>
     *
     * @param code 自定义状态码
     * @param msg 响应消息
     * @return 自定义响应结果对象
     */
    public static Result create(int code, String msg) {
        return new Result(code, msg, null);
    }

    /**
     * 创建自定义响应结果（带IV，无数据）
     * <p>适用于需要自定义状态码和加密消息，但无需返回业务数据的场景</p>
     *
     * @param code 自定义状态码
     * @param msg 响应消息（可能为加密后的密文）
     * @param iv AES加密初始化向量
     * @return 自定义响应结果对象
     */
    public static Result create(int code, String msg, String iv) {
        return new Result(code, msg, null, iv);
    }

    /**
     * 创建自定义响应结果（带数据，无IV）
     * <p>适用于需要自定义状态码和返回数据，但数据无需加密的场景</p>
     *
     * @param code 自定义状态码
     * @param msg 响应消息
     * @param data 业务数据（明文）
     * @return 自定义响应结果对象
     */
    public static Result create(int code, String msg, Object data) {
        return new Result(code, msg, data);
    }

    /**
     * 创建自定义响应结果（带数据和IV）
     * <p>完整的自定义响应构建方法，支持自定义状态码、加密消息、加密数据和IV。</p>
     * <p>适用于需要对响应进行完全控制的加密传输场景</p>
     *
     * @param code 自定义状态码
     * @param msg 响应消息（可能为加密后的密文）
     * @param data 业务数据（可能为加密后的密文）
     * @param iv AES加密初始化向量
     * @return 自定义响应结果对象
     */
    public static Result create(int code, String msg, Object data, String iv) {
        return new Result(code, msg, data, iv);
    }
}
