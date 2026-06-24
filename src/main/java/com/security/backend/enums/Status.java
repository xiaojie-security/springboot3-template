package com.security.backend.enums;


import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@Getter
@AllArgsConstructor
public enum Status {

    ENABLE(1, "启用"),
    SUCCESS(1,"成功"),
    FAIL(0,"失败"),
    DISABLE(0, "禁用");

    /**
     * 指定枚举值在数据库中存储的实际值
     */
    @EnumValue
    private final Integer code;
    private final String desc;

}
