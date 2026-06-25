package com.security.backend.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

import com.security.backend.enums.ResultStatus;
import com.security.backend.enums.Status;
import lombok.Data;

/**
 * 操作日志表
 * @TableName operation_log
 */
@TableName(value ="operation_log")
@Data
public class OperationLog implements Serializable {
    /**
     * 日志ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 操作模块
     */
    @TableField(value = "module")
    private String module;

    /**
     * 操作描述
     */
    @TableField(value = "description")
    private String description;

    /**
     * 请求URL
     */
    @TableField(value = "request_url")
    private String requestUrl;

    /**
     * 请求方法
     */
    @TableField(value = "request_method")
    private String requestMethod;

    /**
     * 请求参数
     */
    @TableField(value = "request_params")
    private String requestParams;

    /**
     * 响应体
     */
    @TableField(value = "response_body")
    private String responseBody;

    /**
     * 操作用户ID
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 操作用户名
     */
    @TableField(value = "username")
    private String username;

    /**
     * 操作IPv4地址
     */
    @TableField(value = "ipv4")
    private String ipv4;

    /**
     * 操作IPv6地址
     */
    @TableField(value = "ipv6")
    private String ipv6;

    /**
     * 操作时间
     */
    @TableField(value = "operation_time")
    private Date operationTime;

    /**
     * 执行耗时(毫秒)
     */
    @TableField(value = "execution_time")
    private Long executionTime;

    /**
     * 1 成功 0 失败
     */
    @TableField(value = "status")
    private ResultStatus status;

    /**
     * 错误信息
     */
    @TableField(value = "error_msg")
    private String errorMsg;

    /**
     * HTTP状态码
     */
    @TableField(value = "http_status")
    private Integer httpStatus;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
