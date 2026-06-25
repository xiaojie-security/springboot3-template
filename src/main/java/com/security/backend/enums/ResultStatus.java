package com.security.backend.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResultStatus {

    SUCCESS(1, "成功"),
    FAIL(0, "失败");

    @EnumValue
    private final Integer code;
    private final String desc;
}
