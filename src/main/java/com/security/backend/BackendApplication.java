package com.security.backend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@SpringBootApplication
@MapperScan(basePackages = "com.security.backend.mapper")
public class BackendApplication {

    public static void main(String[] args) {
        String startupTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        System.setProperty("app.startup-time", startupTime);
        SpringApplication.run(BackendApplication.class, args);
    }

}
