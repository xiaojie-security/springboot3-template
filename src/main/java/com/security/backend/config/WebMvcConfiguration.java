package com.security.backend.config;

import com.security.backend.interceptor.ContextClearInterceptor;
import com.security.backend.interceptor.NonceInterceptor;
import com.security.backend.interceptor.SystemMaintenanceInterceptor;
import com.security.backend.interceptor.TimestampInterceptor;
import com.security.backend.properties.SecurityProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * Spring MVC 配置类。
 * <p>
 * 用于注册自定义拦截器和扩展 MVC 行为。
 * </p>
 */
@Configuration
@RequiredArgsConstructor
public class WebMvcConfiguration implements WebMvcConfigurer {

    private final SecurityProperties securityProperties;

    @Bean
    public ContextClearInterceptor contextClearInterceptor() {
        return new ContextClearInterceptor();
    }

    @Bean
    public NonceInterceptor nonceInterceptor() {
        return new NonceInterceptor();
    }

    @Bean
    public TimestampInterceptor timestampInterceptor() {
        return new TimestampInterceptor();
    }

    @Bean
    public SystemMaintenanceInterceptor systemMaintenanceInterceptor() {
        return new SystemMaintenanceInterceptor();
    }

    /**
     * 注册 MVC 拦截器。
     *
     * @param registry 拦截器注册器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        List<String> excludeUrl = securityProperties.getExcludeUrl();

        registry.addInterceptor(systemMaintenanceInterceptor())
                .order(-1)
                .addPathPatterns("/**")
                .excludePathPatterns(excludeUrl);


        registry.addInterceptor(timestampInterceptor())
                .order(1)
                .addPathPatterns("/**")
                .excludePathPatterns(excludeUrl);

        registry.addInterceptor(nonceInterceptor())
                .order(2)
                .addPathPatterns("/**")
                .excludePathPatterns(excludeUrl);

        registry.addInterceptor(contextClearInterceptor())
                .order(Integer.MAX_VALUE)
                .addPathPatterns("/**");
    }
}
