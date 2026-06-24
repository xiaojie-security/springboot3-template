package com.security.backend.runner;

import com.security.backend.handler.SysConfigHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ConfigCacheLoader implements ApplicationRunner {
    
    private final SysConfigHandler sysConfigHandler;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("ConfigCacheLoader.run 开始加载系统配置到缓存...");
        sysConfigHandler.loadSystemConfigCache();
        log.info("ConfigCacheLoader.run 系统配置加载到缓存完成");
    }
}
