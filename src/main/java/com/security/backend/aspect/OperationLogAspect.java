package com.security.backend.aspect;

import cn.hutool.json.JSONUtil;
import com.security.backend.context.ContextHolder;
import com.security.backend.context.UserContext;
import com.security.backend.domain.OperationLog;
import com.security.backend.enums.ResultStatus;
import com.security.backend.handler.SysConfigHandler;
import com.security.backend.service.OperationLogService;
import com.security.backend.utils.HttpServletUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class OperationLogAspect {

    private final OperationLogService operationLogService;

    private final SysConfigHandler sysConfigHandler;

    /**
     * 参数和响应体最大保存长度（防止大对象导致内存溢出）
     */
    private static final int MAX_CONTENT_LENGTH = 10000;

    /**
     * 切点：标记了@OperationLog注解的方法
     */
    @Pointcut("@annotation(com.security.backend.annotation.OperationLog)")
    public void operationLogPointcut() {
    }

    /**
     * 环绕通知
     */
    @Around("operationLogPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {

        boolean enableOperationLog = sysConfigHandler.enableOperationLog();
        if (!enableOperationLog){
            return joinPoint.proceed();
        }

        long startTime = System.currentTimeMillis();
        OperationLog logEntity = new OperationLog();
        logEntity.setOperationTime(new Date());

        // 获取注解
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        com.security.backend.annotation.OperationLog operationLog = method.getAnnotation(com.security.backend.annotation.OperationLog.class);

        // 获取请求信息
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes != null ? attributes.getRequest() : null;

        HttpServletUtils requestUtils = new HttpServletUtils(request);

        try {
            // 1. 设置模块和描述
            logEntity.setModule(operationLog.module().getName());
            logEntity.setDescription(operationLog.description());

            // 2. 收集请求基本信息
            logEntity.setRequestUrl(request.getRequestURI());
            logEntity.setRequestMethod(request.getMethod());
            logEntity.setIpv4(requestUtils.getIpv4());
            logEntity.setIpv6(requestUtils.getIpv6());

            // 3. 收集请求参数（URL参数）
            if (operationLog.saveRequestParams()) {
                logEntity.setRequestParams(JSONUtil.toJsonStr(joinPoint.getArgs()));
            }


            // 6. 获取用户信息
            UserContext userContext = ContextHolder.getUserContext();
            logEntity.setUserId(userContext.getUserId());
            logEntity.setUsername(userContext.getUserName());

            // 7. 执行原方法
            Object result = joinPoint.proceed();

            // 8. 收集响应体
            if (operationLog.saveResponseBody() && result != null) {
                String responseJson = JSONUtil.toJsonStr(result);
                logEntity.setResponseBody(truncateContent(responseJson));
            }

            // 9. 记录成功状态
            logEntity.setStatus(ResultStatus.SUCCESS);
            logEntity.setHttpStatus(200);
            logEntity.setExecutionTime(System.currentTimeMillis() - startTime);

            return result;

        } catch (Exception e) {
            // 记录失败状态
            logEntity.setStatus(ResultStatus.FAIL);
            logEntity.setErrorMsg(e.getMessage());
            logEntity.setHttpStatus(500);
            logEntity.setExecutionTime(System.currentTimeMillis() - startTime);
            throw e;

        } finally {
            // 异步保存日志
            CompletableFuture.runAsync(() -> operationLogService.save(logEntity));
        }
    }

    /**
     * 截断过长的内容
     */
    private String truncateContent(String content) {
        if (content == null) {
            return null;
        }
        if (content.length() > MAX_CONTENT_LENGTH) {
            return content.substring(0, MAX_CONTENT_LENGTH) + "... (truncated)";
        }
        return content;
    }

}
