package com.security.backend.strategy.login.impl;

import com.security.backend.domain.result.LoginResult;
import com.security.backend.enums.LoginChannel;
import com.security.backend.strategy.login.UserLoginStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class EmailCodeUserLoginStrategy implements UserLoginStrategy {
    @Override
    public LoginChannel getLoginChannel() {
        return LoginChannel.EMAIL_CODE;
    }

    @Override
    public LoginResult login(String principal, String credentials) {
        return null;
    }
}
