package com.security.backend.context;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class UserContext {
    private static final ThreadLocal<UserInfo> userLocal = new ThreadLocal<>();

    public UserContext() {
    }

    public void clear() {
        userLocal.remove();
    }

    public String getUserName() {
        UserInfo userInfo = userLocal.get();
        return userInfo != null ? userInfo.getUserName() : null;
    }

    public String getUserToken() {
        UserInfo userInfo = userLocal.get();
        return userInfo != null ? userInfo.getUserToken() : null;
    }

    public Long getUserId() {
        UserInfo userInfo = userLocal.get();
        return userInfo != null ? userInfo.getUserId() : null;
    }

    public void setUserName(String userName) {
        UserInfo userInfo = userLocal.get();
        if (userInfo == null) {
            userInfo = new UserInfo();
        }
        userInfo.setUserName(userName);
        userLocal.set(userInfo);
    }

    public void setUserToken(String userToken) {
        UserInfo userInfo = userLocal.get();
        if (userInfo == null) {
            userInfo = new UserInfo();
        }
        userInfo.setUserToken(userToken);
        userLocal.set(userInfo);
    }

    public void setUserId(Long userId) {
        UserInfo userInfo = userLocal.get();
        if (userInfo == null) {
            userInfo = new UserInfo();
        }
        userInfo.setUserId(userId);
        userLocal.set(userInfo);
    }

    public void setUserInfo(Long userId, String userName, String userToken) {
        UserInfo userInfo = userLocal.get();
        if (userInfo == null) {
            userInfo = new UserInfo();
        }
        userInfo.setUserId(userId);
        userInfo.setUserToken(userToken);
        userInfo.setUserName(userName);
        userLocal.set(userInfo);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class UserInfo {
        private String userName;
        private String userToken;
        private Long userId;
    }
}
