package com.security.backend.strategy.login;

import com.security.backend.domain.result.LoginResult;
import com.security.backend.enums.LoginChannel;

public interface UserLoginStrategy {


    /**
     * 获取登入类型
     */
    LoginChannel getLoginChannel();

    /**
     * 登录
     * @param principal 登录主体
     * @param credentials 登录凭证
     * @return 身份凭证
     */
    LoginResult login(String principal, String credentials);

}
