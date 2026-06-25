package com.security.backend.config;

import cn.hutool.core.collection.CollUtil;
import com.security.backend.authentication.CustomAccessDeniedHandler;
import com.security.backend.authentication.CustomAuthenticationEntryPoint;
import com.security.backend.filter.*;
import com.security.backend.handler.UserDetailsServiceHandler;
import com.security.backend.properties.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

/**
 * Spring Security 配置类。
 * <p>
 * 负责配置认证授权规则、过滤器链、密码编码器以及安全相关的基础 Bean。
 * </p>
 */
@Configuration
@EnableWebSecurity(debug = true)
@EnableMethodSecurity
public class SecurityConfiguration {

    /**
     * 安全配置属性 Bean。
     *
     * @return 安全配置属性对象
     */
    @Bean
    public SecurityProperties securityProperties() {
        return new SecurityProperties();
    }

    /**
     * 构建 Spring Security 过滤器链。
     *
     * @param httpSecurity HttpSecurity 构建器
     * @return 安全过滤器链
     * @throws Exception 配置安全链时发生异常
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        SecurityProperties securityProperties = securityProperties();

        List<String> excludeUrl = securityProperties.getExcludeUrl();

        httpSecurity.authorizeHttpRequests(
                        authorizeHttpRequests -> {
                            if (CollUtil.isNotEmpty(excludeUrl)) {
                                excludeUrl.forEach(url -> authorizeHttpRequests.requestMatchers(url).permitAll());
                            }
                            authorizeHttpRequests.anyRequest().authenticated();
                        })
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .cors(withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .httpBasic(AbstractHttpConfigurer::disable);

        httpSecurity.addFilterBefore(contextAssemblyFilter(), UsernamePasswordAuthenticationFilter.class);
        httpSecurity.addFilterBefore(tokenAuthenticationFilter(), ContextAssemblyFilter.class);
        httpSecurity.addFilterBefore(resolveTokenFilter(), TokenAuthenticationFilter.class);
        httpSecurity.addFilterAfter(requestCachingFilter(), CorsFilter.class);
        httpSecurity.addFilterAfter(requestLegalFilter(), RequestCachingFilter.class);


        // 配置异常处理
        httpSecurity.exceptionHandling(httpSecurityExceptionHandlingConfigurer -> {
            httpSecurityExceptionHandlingConfigurer.authenticationEntryPoint(new CustomAuthenticationEntryPoint());
            httpSecurityExceptionHandlingConfigurer.accessDeniedHandler(new CustomAccessDeniedHandler());
        });
        return httpSecurity.build();
    }

    /**
     * 创建密码编码器。
     *
     * @return BCrypt 密码编码器
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(8);
    }


    @Bean
    public TokenAuthenticationFilter tokenAuthenticationFilter() {
        return new TokenAuthenticationFilter();
    }


    @Bean
    public ResolveTokenFilter resolveTokenFilter() {
        return new ResolveTokenFilter(securityProperties());
    }

    @Bean
    public RequestCachingFilter requestCachingFilter() {
        return new RequestCachingFilter();
    }

    @Bean
    public ContextAssemblyFilter contextAssemblyFilter() {
        return new ContextAssemblyFilter();
    }

    @Bean
    public RequestLegalFilter requestLegalFilter() {
        return new RequestLegalFilter();
    }


    @Bean
    public UserDetailsService userDetailsService(){
        return new UserDetailsServiceHandler();
    }
}
