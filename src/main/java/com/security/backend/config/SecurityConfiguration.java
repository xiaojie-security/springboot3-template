package com.security.backend.config;

import cn.hutool.core.collection.CollUtil;
import com.security.backend.authentication.AuthenticationEntryPointImpl;
import com.security.backend.filter.RequestCachingFilter;
import com.security.backend.filter.ResolveTokenFilter;
import com.security.backend.filter.TokenAuthenticationFilter;
import com.security.backend.handler.UserDetailsServiceHandler;
import com.security.backend.properties.SecurityProperties;
import lombok.RequiredArgsConstructor;
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
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

/**
 * Spring Security 配置类。
 * <p>
 * 负责配置认证授权规则、过滤器链、密码编码器以及安全相关的基础 Bean。
 * </p>
 */
@Configuration
@EnableWebSecurity
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


        httpSecurity.addFilterBefore(requestCachingFilter(),UsernamePasswordAuthenticationFilter.class);
        httpSecurity.addFilterAfter(resolveTokenFilter(), RequestCachingFilter.class);
        httpSecurity.addFilterAfter(tokenAuthenticationFilter(), ResolveTokenFilter.class);

        // 配置异常处理
        httpSecurity.exceptionHandling(httpSecurityExceptionHandlingConfigurer -> {
            httpSecurityExceptionHandlingConfigurer.authenticationEntryPoint(new AuthenticationEntryPointImpl());
            httpSecurityExceptionHandlingConfigurer.accessDeniedHandler(new AccessDeniedHandlerImpl());
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

    /**
     * 创建 token 认证过滤器。
     *
     * @return token 认证过滤器
     */
    @Bean
    public TokenAuthenticationFilter tokenAuthenticationFilter() {
        return new TokenAuthenticationFilter();
    }

    /**
     * 创建 token 解析过滤器。
     *
     * @return token 解析过滤器
     */
    @Bean
    public ResolveTokenFilter resolveTokenFilter() {
        return new ResolveTokenFilter(securityProperties());
    }

    @Bean
    public RequestCachingFilter requestCachingFilter() {
        return new RequestCachingFilter();
    }


    @Bean
    public UserDetailsService userDetailsService(){
        return new UserDetailsServiceHandler();
    }
}
