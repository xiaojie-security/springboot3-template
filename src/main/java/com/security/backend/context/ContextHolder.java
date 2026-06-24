package com.security.backend.context;

/**
 * 请求上下文持有器。
 */
public class ContextHolder {

    /**
     * 用户上下文。
     */
    private static UserContext USER_CONTEXT = new UserContext();

    /**
     * 加密上下文。
     */
    private static EncryptContext ENCRYPT_CONTEXT = new EncryptContext();

    /**
     * 获取用户上下文。
     *
     * @return 用户上下文
     */
    public static UserContext getUserContext() {
        return USER_CONTEXT;
    }

    /**
     * 设置用户上下文。
     *
     * @param userContext 用户上下文
     */
    public static void setUserContext(UserContext userContext) {
        USER_CONTEXT = userContext;
    }

    /**
     * 设置加密上下文。
     *
     * @param encryptContext 加密上下文
     */
    public static void setEncryptContext(EncryptContext encryptContext) {
        ENCRYPT_CONTEXT = encryptContext;
    }

    /**
     * 获取加密上下文。
     *
     * @return 加密上下文
     */
    public static EncryptContext getEncryptContext() {
        return ENCRYPT_CONTEXT;
    }



    /**
     * 清理当前线程中的所有上下文。
     */
    public static void clear() {
        USER_CONTEXT.clear();
        ENCRYPT_CONTEXT.clear();
    }
}
