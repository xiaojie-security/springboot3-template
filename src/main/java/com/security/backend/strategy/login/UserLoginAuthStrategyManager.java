package com.security.backend.strategy.login;

import com.security.backend.enums.LoginChannel;
import com.security.backend.exception.AuthenticationBusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * 用户登录策略管理器
 */
@Component
@Slf4j
public class UserLoginAuthStrategyManager {


    private final Map<LoginChannel, UserLoginStrategy> strategyMap;


    public UserLoginAuthStrategyManager(List<UserLoginStrategy> strategyList) {
        if (CollectionUtils.isEmpty(strategyList)) {
            this.strategyMap = new LinkedHashMap<>();
            return;
        }
        this.strategyMap = strategyList.stream().collect(Collectors.toMap(
                UserLoginStrategy::getLoginChannel,
                item -> item,
                (left, right) -> left,
                LinkedHashMap::new
        ));
    }

    public UserLoginStrategy getStrategy(LoginChannel loginChannel) {
        UserLoginStrategy strategy = strategyMap.get(loginChannel);
        if (strategy == null) {
            log.error("UserLoginAuthStrategyManager.getStrategy 未找到登入策略，loginChannel={}", loginChannel);
            throw AuthenticationBusinessException.AUTHENTICATION_METHOD_ABNORMAL;
        }
        return strategy;
    }

    public UserLoginStrategy getStrategy(String loginChannel) {
        LoginChannel channel = LoginChannel.of(loginChannel);
        return getStrategy(channel);
    }
}

