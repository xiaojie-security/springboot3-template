package com.security.backend.handler;


import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class JwtTokenHandler {

    @Resource
    @Lazy
    private SysConfigHandler sysConfigHandler;

    public static final String USERNAME_KEY = "username";
    public static final String USERID_KEY = "user_id";

    /**
     * 生成token
     * @return
     */
    public String createAccessToken(Map<String, Object> payload , String secret) {
        JwtBuilder builder = Jwts.builder();
        builder.claims(payload);
        builder.expiration(getExpirationTime(sysConfigHandler.queryTokenExpireSeconds()));  // 设置过期时
        builder.signWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)));
        builder.issuedAt(new Date());
        return builder.compact();
    }

    /**
     * 生成token
     * @return
     */
    public String createAccessToken(String username, Long userId , String secret) {
        return createAccessToken(Map.of(
                USERNAME_KEY,username,
                USERID_KEY,userId
        ),secret);
    }

    /**
     * 生成token
     * @return
     */
    public String createRefreshToken(String secret) {
        JwtBuilder builder = Jwts.builder();
        builder.expiration(getExpirationTime(sysConfigHandler.queryTokenExpireSeconds() * 3));  // 设置过期时
        builder.signWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)));
        builder.issuedAt(new Date());
        return builder.compact();
    }

    /**
     * 获取用户名
     */
    public Object get(String token, String secret, String key) {
        // 得到解析结果
        Jws<Claims> claimsJws = parse(token,secret);
        if (claimsJws == null){
            return null;
        }
        // 得到载荷对象
        Claims payload = claimsJws.getPayload();

        return payload.get(key);
    }

    /**
     * 获取用户名
     */
    public <T> T get(String token, String secret, String key, Class<T> clazz) {
        // 得到解析结果
        Jws<Claims> claimsJws = parse(token,secret);
        if (claimsJws == null){
            return null;
        }
        // 得到载荷对象
        Claims payload = claimsJws.getPayload();

        return payload.get(key,clazz);
    }

    public String getUsername(String token, String secret) {
        return get(token,secret,USERNAME_KEY,String.class);
    }

    public <T> T getUserId(String token, String secret, Class<T> clazz) {
        return get(token,secret,USERID_KEY,clazz);
    }

    /**
     * 判断token是否过期
     */
    public boolean validate(String token, String secret) {
        // 得到解析结果
        Jws<Claims> claimsJws = parse(token,secret);
        if (claimsJws == null){
            return false;
        }
        // 得到载荷对象
        Claims payload = claimsJws.getPayload();
        // 得到过期时间
        Date expiration = payload.getExpiration();
        // 判断是否过期   使用 before 方法比较当前时间和token中的过期时间
        // 如果当前时间在过期时间之前，则返回 true，表示token未过期；否则返回 false，表示token已过期
        return new Date().before(expiration);
    }

    private Jws<Claims> parse(String token, String secret) {
        // 使用相同的Key解析令牌 得到解析构造器 并且设置相同的Key
        JwtParser parser = Jwts.parser().verifyWith(
                Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)))
                .build();
        // 获取解析结果
        try {
            return parser.parseSignedClaims(token);
        } catch (ExpiredJwtException e){
            return null;
        }
    }

    /**
     * 获取过期时间
     *
     * @param seconds 过期秒数（正数表示未来时间，负数或0表示当前时间）
     * @return 过期时间
     */
    private Date getExpirationTime(Long seconds) {
        if (seconds == null || seconds <= 0) {
            return new Date();
        }
        return new Date(System.currentTimeMillis() + seconds * 1000);
    }

}
