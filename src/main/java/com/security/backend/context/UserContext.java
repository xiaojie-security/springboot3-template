package com.security.backend.context;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class UserContext {
    private static final ThreadLocal<UserInfo> userInfo = new ThreadLocal<>();

    public UserContext() {
    }

    public void clear() {
        userInfo.remove();
    }

    private UserInfo get() {
        return userInfo.get();
    }

    private void set(UserInfo info) {
        userInfo.set(info);
    }

    public String getUserName() {
        UserInfo userInfo = get();
        return userInfo != null ? userInfo.getUserName() : null;
    }

    public String getUserToken() {
        UserInfo userInfo = get();
        return userInfo != null ? userInfo.getUserToken() : null;
    }

    public Long getUserId() {
        UserInfo userInfo = get();
        return userInfo != null ? userInfo.getUserId() : null;
    }

    public void setUserName(String userName) {
        UserInfo userInfo = get();
        if (userInfo == null) {
            userInfo = new UserInfo();
        }
        userInfo.setUserName(userName);
        set(userInfo);
    }

    public void setUserToken(String userToken) {
        UserInfo userInfo = get();
        if (userInfo == null) {
            userInfo = new UserInfo();
        }
        userInfo.setUserToken(userToken);
        set(userInfo);
    }

    public void setUserId(Long userId) {
        UserInfo userInfo = get();
        if (userInfo == null) {
            userInfo = new UserInfo();
        }
        userInfo.setUserId(userId);
        set(userInfo);
    }

    public void setUserInfo(Long userId, String userName, String userToken) {
        UserInfo userInfo = get();
        if (userInfo == null) {
            userInfo = new UserInfo();
        }
        userInfo.setUserId(userId);
        userInfo.setUserToken(userToken);
        userInfo.setUserName(userName);
        set(userInfo);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    class UserInfo {
        private String userName;
        private String userToken;
        private Long userId;
    }
}
