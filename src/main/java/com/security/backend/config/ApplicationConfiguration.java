package com.security.backend.config;

import com.security.backend.config.properties.AuthProperties;
import com.security.backend.config.properties.RsaEncryptProperties;
import com.security.backend.config.properties.TokenProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(value = {
        AuthProperties.class,
        RsaEncryptProperties.class,
        TokenProperties.class
})
public class ApplicationConfiguration {

}
