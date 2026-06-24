package com.security.backend.domain.vo;

import lombok.Data;

import java.util.Date;

/**
 * 系统配置返回对象。
 */
@Data
public class SysConfigVo {

    /**
     * 主键ID。
     */
    private Long id;

    /**
     * 配置KEY。
     */
    private String configKey;

    /**
     * 配置值。
     */
    private String configValue;

    /**
     * 配置类型。
     */
    private String configType;

    /**
     * 配置名称。
     */
    private String configName;

    /**
     * 备注。
     */
    private String remark;

    /**
     * 状态。
     */
    private Integer status;

    /**
     * 创建时间。
     */
    private Date createTime;

    /**
     * 更新时间。
     */
    private Date updateTime;
}
