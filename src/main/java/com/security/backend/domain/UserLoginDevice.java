package com.security.backend.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

import com.security.backend.enums.DeviceType;
import com.security.backend.enums.Status;
import com.security.backend.enums.ValidStatus;
import lombok.Data;

/**
 * 用户登录设备表
 * @TableName user_login_device
 */
@TableName(value ="user_login_device")
@Data
public class UserLoginDevice implements Serializable {
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 设备唯一标识
     */
    @TableField(value = "device_id")
    private String deviceId;

    /**
     * 设备类型: PC/MOBILE/TABLET/UNKNOWN
     */
    @TableField(value = "device_type")
    private DeviceType deviceType;

    /**
     * 设备名称（用户自定义）
     */
    @TableField(value = "device_name")
    private String deviceName;

    /**
     * 访问令牌
     */
    @TableField(value = "access_token")
    private String accessToken;

    /**
     * 刷新令牌
     */
    @TableField(value = "refresh_token")
    private String refreshToken;

    /**
     * 登录IP地址
     */
    @TableField(value = "login_ip")
    private String loginIp;

    /**
     * 登录地理位置
     */
    @TableField(value = "login_location")
    private String loginLocation;

    /**
     * 浏览器User-Agent
     */
    @TableField(value = "user_agent")
    private String userAgent;

    /**
     * 登录时间
     */
    @TableField(value = "login_time")
    private Date loginTime;

    /**
     * Token过期时间
     */
    @TableField(value = "expire_time")
    private Date expireTime;

    /**
     * 最后活跃时间
     */
    @TableField(value = "last_active_time")
    private Date lastActiveTime;

    /**
     * 登录状态: 1-有效 0-失效
     */
    @TableField(value = "status")
    private ValidStatus status;

    /**
     * 被踢原因
     */
    @TableField(value = "kick_reason")
    private String kickReason;

    /**
     * 被踢时间
     */
    @TableField(value = "kick_time")
    private Date kickTime;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time")
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
