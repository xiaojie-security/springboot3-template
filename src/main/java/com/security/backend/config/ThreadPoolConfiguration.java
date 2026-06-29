package com.security.backend.config;

import com.security.backend.decorator.ContextCopyingDecorator;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
@Slf4j
public class ThreadPoolConfiguration implements ApplicationContextAware , DisposableBean {

    private static final int cpuCores = Runtime.getRuntime().availableProcessors();

    private ApplicationContext applicationContext;

    /**
     * 1. 核心业务线程池 - 处理订单、支付等关键链路
     * 特点：资源倾斜，拒绝策略直接抛出异常（让调用方感知）
     */
    @Bean("coreBusinessExecutor")
    public ThreadPoolTaskExecutor coreBusinessExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // 核心线程数：CPU密集型，设为核心数+1
        executor.setCorePoolSize( + 1);
        // 最大线程数：允许临时扩容
        executor.setMaxPoolSize(cpuCores * 2);
        // 空闲存活时间：60秒回收非核心线程
        executor.setKeepAliveSeconds(60);
        // 队列容量：使用有界队列，防止任务堆积
        executor.setQueueCapacity(200);
        // 线程名前缀（便于排查问题）
        executor.setThreadNamePrefix("core-biz-");
        // 拒绝策略：直接抛出 RejectedExecutionException（快速失败）
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        // 允许核心线程超时回收（如果流量长期很低，释放资源）
        executor.setAllowCoreThreadTimeOut(true);
        // 预热：提前创建核心线程（可选，视情况开启）
        // executor.setPrestartAllCoreThreads(true);
        executor.setTaskDecorator(new ContextCopyingDecorator());
        executor.initialize();
        return executor;
    }

    /**
     * 2. 非核心/批量任务线程池 - 处理报表导出、邮件推送、数据同步
     * 特点：允许队列堆积，拒绝策略采用调用者运行（降级）
     */
    @Bean("batchExecutor")
    public ThreadPoolTaskExecutor batchExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // 核心线程数：较少，避免浪费资源
        executor.setCorePoolSize(5);
        // 最大线程数：适度扩容
        executor.setMaxPoolSize(20);
        // 空闲存活时间：30秒回收
        executor.setKeepAliveSeconds(30);
        // 队列容量：较大，允许任务排队等待（但如果持续堆积要报警）
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("batch-");
        // 拒绝策略：调用者运行（让提交任务的线程自己执行，起到降级限流作用）
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 允许核心线程超时
        executor.setAllowCoreThreadTimeOut(true);
        executor.setTaskDecorator(new ContextCopyingDecorator());
        executor.initialize();
        return executor;
    }

    /**
     * 3. IO密集型任务线程池 - 处理大量外部API调用、数据库查询
     * 特点：线程数较多，充分利用等待时间
     */
    @Bean("ioIntensiveExecutor")
    public ThreadPoolTaskExecutor ioIntensiveExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // 核心线程数：IO密集，建议设置为 CPU核心数 * 2~4 倍
        executor.setCorePoolSize(cpuCores * 4);
        // 最大线程数：可以更大
        executor.setMaxPoolSize(cpuCores * 8);
        // 空闲存活时间：60秒
        executor.setKeepAliveSeconds(60);
        // 队列容量：适中，不要太大
        executor.setQueueCapacity(300);
        executor.setThreadNamePrefix("io-");
        // 拒绝策略：直接抛出异常（避免任务无限堆积导致内存溢出）
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        // 允许核心线程超时
        executor.setAllowCoreThreadTimeOut(true);
        executor.setTaskDecorator(new ContextCopyingDecorator());
        executor.initialize();
        return executor;
    }

    @Override
    public void destroy() throws Exception {
        Map<String, ThreadPoolTaskExecutor> beansOfType = applicationContext.getBeansOfType(ThreadPoolTaskExecutor.class);
        for (Map.Entry<String, ThreadPoolTaskExecutor> entry : beansOfType.entrySet()) {
            try {
                shutdownExecutor(entry.getKey(), entry.getValue());
            } catch (Exception e) {
                log.error("ThreadPoolConfiguration.destroy 关闭线程池 {} 时发生异常", entry.getKey(), e);
            }
        }
    }

    private void shutdownExecutor(String poolName, ThreadPoolTaskExecutor executor) {
        if (executor != null) {
            log.info("ThreadPoolConfiguration.shutdownExecutor 开始关闭线程池: {}", poolName);
            executor.shutdown();
            try {
                if (!executor.getThreadPoolExecutor().awaitTermination(30, TimeUnit.SECONDS)) {
                    log.warn("ThreadPoolConfiguration.shutdownExecutor 线程池 {} 未能优雅关闭，强制终止", poolName);
                    executor.getThreadPoolExecutor().shutdownNow();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                executor.getThreadPoolExecutor().shutdownNow();
            }
            log.info("ThreadPoolConfiguration.shutdownExecutor 线程池 {} 关闭完成", poolName);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
