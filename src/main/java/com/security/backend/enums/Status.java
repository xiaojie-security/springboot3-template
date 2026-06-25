package com.security.backend.enums;


import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@Getter
@AllArgsConstructor
public enum Status {

    ENABLE(1, "启用"),
    DISABLE(0, "禁用");

    @EnumValue
    private final Integer code;
    private final String desc;

    public static Status fromCode(Integer code) {
        for (Status status : Status.values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}
