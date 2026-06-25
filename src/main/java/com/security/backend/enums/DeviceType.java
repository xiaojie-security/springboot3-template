package com.security.backend.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum DeviceType {
    PC("PC"),
    MOBILE("MOBILE"),
    TABLET("TABLET"),
    UNKNOWN("UNKNOWN");

    @EnumValue
    private final String value;
}
