package com.security.backend.decorator;

import com.security.backend.context.ContextHolder;
import com.security.backend.context.EncryptContext;
import com.security.backend.context.RequestContext;
import com.security.backend.context.UserContext;
import org.slf4j.MDC;
import org.springframework.core.task.TaskDecorator;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.Map;

/**
 * 上下文拷贝装饰器
 * 作用：将主线程的 MDC、RequestAttributes 等上下文传递给子线程
 */
public class ContextCopyingDecorator implements TaskDecorator {

    @Override
    public Runnable decorate(Runnable runnable) {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        SecurityContext securityContext = SecurityContextHolder.getContext();
        EncryptContext encryptContext = ContextHolder.getEncryptContext();
        UserContext userContext = ContextHolder.getUserContext();
        RequestContext requestContext = ContextHolder.getRequestContext();

        // 3. 返回包装后的 Runnable
        return () -> {
            try {
                if (requestAttributes != null) {
                    RequestContextHolder.setRequestAttributes(requestAttributes);
                }

                if (securityContext != null) {
                    SecurityContextHolder.setContext(securityContext);
                }

                if (userContext != null) {
                    ContextHolder.setUserContext(userContext);
                }

                if (encryptContext != null) {
                    ContextHolder.setEncryptContext(encryptContext);
                }
                if (requestContext != null) {
                    ContextHolder.setRequestContext(requestContext);
                }
                
                runnable.run();
                
            } finally {
                RequestContextHolder.resetRequestAttributes();
                ContextHolder.clear();
            }
        };
    }
}
