package com.security.backend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

/**
 * Jackson 序列化配置类。
 * <p>
 * 负责定制全局 ObjectMapper，处理 Long 精度保护和 Java 时间类型序列化。
 * </p>
 */
@Configuration
public class JacksonConfiguration {

    /**
     * 创建全局 ObjectMapper。
     * <p>
     * Long 类型序列化为字符串，避免前端 JavaScript 数字精度丢失；
     * 同时注册 JavaTimeModule 支持 Java 8 时间类型。
     * </p>
     *
     * @param builder Jackson ObjectMapper 构建器
     * @return 全局 ObjectMapper
     */
    @Bean
    public ObjectMapper jacksonObjectMapper(Jackson2ObjectMapperBuilder builder) {
        ObjectMapper objectMapper = builder.createXmlMapper(false).build();
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
        objectMapper.registerModule(simpleModule);
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }
}
