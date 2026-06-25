package com.security.backend.enums;

import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public enum LoginChannel {

    USERNAME_PASSWORD("username_password", "用户名密码登录"),
    PHONE_CODE("phone_code", "手机号验证码登录"),
    EMAIL_CODE("email_code", "邮箱验证码登录");


    private final String code;
    private final String desc;

    private static final Map<String, LoginChannel> LOGIN_CHANNEL_ENUM_MAP = Arrays.stream(values())
            .collect(Collectors.toMap(LoginChannel::getCode, Function.identity()));

    /**
     * 根据code获取枚举
     * @param code 渠道代码
     * @return 对应的枚举，不存在时返回null
     */
    public static LoginChannel of(String code) {
        return StrUtil.isEmpty(code) ? null : LOGIN_CHANNEL_ENUM_MAP.get(code);
    }

    /**
     * 检查是否支持该渠道
     * @param code 渠道代码
     * @return 是否支持
     */
    public static boolean isSupported(String code) {
        return StrUtil.isNotEmpty(code) && LOGIN_CHANNEL_ENUM_MAP.containsKey(code);
    }
}
