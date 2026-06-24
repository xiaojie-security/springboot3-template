package com.security.backend.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

import com.security.backend.enums.Status;
import lombok.Data;

/**
 * 系统配置表
 * @TableName sys_config
 */
@TableName(value ="sys_config")
@Data
public class SysConfig implements Serializable {
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 配置KEY
     */
    @TableField(value = "config_key")
    private String configKey;

    /**
     * 配置值
     */
    @TableField(value = "config_value")
    private String configValue;

    /**
     * 配置类型
     */
    @TableField(value = "config_type")
    private String configType;

    /**
     * 配置名称
     */
    @TableField(value = "config_name")
    private String configName;

    /**
     * 备注
     */
    @TableField(value = "remark")
    private String remark;

    /**
     * 状态(1启用 0禁用)
     */
    @TableField(value = "status")
    private Status status;

    /**
     * 
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 
     */
    @TableField(value = "update_time")
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}
