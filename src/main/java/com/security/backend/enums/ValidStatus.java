package com.security.backend.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ValidStatus {

    VALID(1, "有效"),
    INVALID(0, "失效");

    @EnumValue
    private final Integer code;
    private final String desc;
}
