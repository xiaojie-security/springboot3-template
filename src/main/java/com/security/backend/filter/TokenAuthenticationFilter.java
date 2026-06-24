package com.security.backend.filter;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpStatus;
import cn.hutool.json.JSONUtil;

import com.security.backend.context.ContextHolder;
import com.security.backend.handler.JwtTokenHandler;
import com.security.backend.handler.RedisKeysHandler;
import com.security.backend.properties.SecurityProperties;
import com.security.backend.utils.HttpUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * token 过滤拦截器
 */
@Slf4j
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    @Resource
    private SecurityProperties securityProperties;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private JwtTokenHandler jwtTokenHandler;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Object tokenAttribute = request.getAttribute(securityProperties.getAccess().getHeader());
        String token = ObjUtil.defaultIfNull(tokenAttribute, "").toString();

        // 判断token是否为空，如果为空则放行请求（允许匿名访问）
        if (StrUtil.isEmpty(token)) {
            // 继续执行过滤器链，放行请求
            filterChain.doFilter(request,response);
            // 直接返回，不再执行后续逻辑
            return;
        }

        // 获取JWT签名密钥，用于解析和验证token
        String accessSecretKey = securityProperties.getAccess().getSecret();
        // 声明用户名变量，用于存储从token中解析出的用户名
        String username;
        // 声明用户ID变量，用于存储从token中解析出的用户ID
        Long userId;
        try {
            // 使用JWT工具类解析token，提取用户名信息
            username = jwtTokenHandler.get(token, accessSecretKey, "username",String.class);
            // 使用JWT工具类解析token，提取用户ID信息
            userId = jwtTokenHandler.get(token, accessSecretKey, "userId",Long.class);
        } catch (Exception e) {
            // 如果解析token失败（token过期、格式错误等），返回401未授权响应
            log.debug("TokenAuthenticationFilter.doFilterInternal 获取token失败",e);
            HttpUtils.writeResponseBody(response, HttpStatus.HTTP_UNAUTHORIZED, "unauthorized 身份校验失败 请重新登入");
            // 终止过滤链执行
            return;
        }
        // 验证用户名是否为空，如果为空说明token无效
        if (StrUtil.isEmptyIfStr(username)) {
            // 返回401未授权响应
            log.debug("TokenAuthenticationFilter.doFilterInternal 获取用户名为空");
            HttpUtils.writeResponseBody(response, HttpStatus.HTTP_UNAUTHORIZED, "unauthorized 身份校验失败 请重新登入");
            // 终止过滤链执行
            return;
        }
        // 验证用户ID是否为空，如果为空说明token无效
        if (ObjUtil.isEmpty(userId)) {
            // 返回401未授权响应
            log.debug("TokenAuthenticationFilter.doFilterInternal 获取用户ID为空");
            HttpUtils.writeResponseBody(response, HttpStatus.HTTP_UNAUTHORIZED, "unauthorized 身份校验失败 请重新登入");
            // 终止过滤链执行
            return;
        }

        // 判断Redis中是否存在该用户的token，如果不存在说明token已过期或用户已登出
        if (!jwtTokenHandler.validate(token, accessSecretKey)) {
            // 返回401未授权响应，提示用户重新登录
            log.debug("TokenAuthenticationFilter.doFilterInternal 获取token已过期");
            HttpUtils.writeResponseBody(response, HttpStatus.HTTP_UNAUTHORIZED, "unauthorized 身份标识已过期 请重新登入");
            // 终止过滤链执行
            return;
        }


        // 从Redis中获取该用户的权限列表字符串
        String perms = stringRedisTemplate.opsForValue().get(
                // 根据用户ID构建Redis中存储权限信息的key
                RedisKeysHandler.getUserPermission(userId)
        );
        // 创建空的权限列表，用于存储用户的权限编码
        List<String> permissions = new ArrayList<>();
        // 判断权限字符串是否不为空
        if (StrUtil.isNotEmpty(perms)) {
            // 将JSON格式的权限字符串转换为List<String>集合
            permissions = JSONUtil.toList(perms, String.class);
        }

        // 创建Spring Security的认证令牌对象，包含用户名、密码（null表示已认证）、权限列表
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(username,null,toAuthorities(permissions));
        // 将认证令牌设置到SecurityContextHolder上下文中，供后续Spring Security使用
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        try {
            // 继续执行过滤器链，将请求传递给下一个过滤器或目标资源
            filterChain.doFilter(request,response);
        } finally {
            ContextHolder.clear();
            SecurityContextHolder.clearContext();
        }
    }

    /**
     * 将权限字符串列表转换为Spring Security的授权对象列表
     * @param permissions 权限编码列表
     * @return Spring Security授权对象列表
     */
    private List<SimpleGrantedAuthority> toAuthorities(List<String> permissions){
        // 创建授权对象列表
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        // 遍历每个权限编码
        for (String permission : permissions) {
            // 将权限编码包装成SimpleGrantedAuthority对象并添加到列表中
            authorities.add(new SimpleGrantedAuthority(permission));
        }
        // 返回授权对象列表
        return authorities;
    }
}
