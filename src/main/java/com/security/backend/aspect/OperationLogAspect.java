package com.security.backend.aspect;

import cn.hutool.json.JSONUtil;
import com.security.backend.context.ContextHolder;
import com.security.backend.context.UserContext;
import com.security.backend.domain.OperationLog;
import com.security.backend.enums.Status;
import com.security.backend.handler.SysConfigHandler;
import com.security.backend.service.OperationLogService;
import com.security.backend.utils.IpUtils;
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

        try {
            // 1. 设置模块和描述
            logEntity.setModule(operationLog.module().getName());
            logEntity.setDescription(operationLog.description());

            // 2. 收集请求基本信息
            if (request != null) {
                logEntity.setRequestUrl(request.getRequestURL().toString());
                logEntity.setRequestMethod(request.getMethod());
                logEntity.setIpv4(IpUtils.getClientIpv4(request));
                logEntity.setIpv6(IpUtils.getClientIpv6(request));

                // 3. 收集请求参数（URL参数）
                if (operationLog.saveRequestParams()) {
                    logEntity.setRequestParams(JSONUtil.toJsonStr(joinPoint.getArgs()));
                }
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
            logEntity.setStatus(Status.SUCCESS);
            logEntity.setHttpStatus(200);
            logEntity.setExecutionTime(System.currentTimeMillis() - startTime);

            return result;

        } catch (Exception e) {
            // 记录失败状态
            logEntity.setStatus(Status.FAIL);
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
     * 获取方法参数（当请求体为空时的备选方案）
     */
    private String getMethodParams(ProceedingJoinPoint joinPoint) {
        try {
            Object[] args = joinPoint.getArgs();
            if (args == null || args.length == 0) {
                return null;
            }

            // 过滤掉HttpServletRequest/Response等非业务参数
            List<Object> businessParams = Arrays.stream(args)
                    .filter(arg -> !(arg instanceof HttpServletRequest))
                    .filter(arg -> !(arg instanceof HttpServletResponse))
                    .filter(arg -> !(arg instanceof MultipartFile))
                    .collect(Collectors.toList());

            if (businessParams.isEmpty()) {
                return null;
            }

            String paramsJson = JSONUtil.toJsonStr(businessParams);
            return truncateContent(paramsJson);

        } catch (Exception e) {
            log.warn("OperationLogAspect.getMethodParams 获取方法参数失败", e);
            return null;
        }
    }

    /**
     * 判断是否为Body请求
     */
    private boolean isBodyRequest(HttpServletRequest request) {
        String method = request.getMethod();
        String contentType = request.getContentType();

        if (contentType == null) {
            return false;
        }

        // POST/PUT/PATCH 且非表单/文件上传
        if ("POST".equalsIgnoreCase(method) ||
                "PUT".equalsIgnoreCase(method) ||
                "PATCH".equalsIgnoreCase(method)) {

            return contentType.contains("application/json") ||
                    contentType.contains("application/xml") ||
                    contentType.contains("text/plain");
        }

        return false;
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
