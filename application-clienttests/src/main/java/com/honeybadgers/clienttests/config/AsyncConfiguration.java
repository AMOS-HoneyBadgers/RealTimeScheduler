package com.honeybadgers.clienttests.config;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfiguration {
    private static final Logger LOGGER = LogManager.getLogger(AsyncConfiguration.class);

    /**
     * Executor thread pool for HTTP performance tests calls with size 2
     * @return Executor Object
     */
    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        LOGGER.debug("Creating Async Task Executor");
        final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(2);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("ScheduleThread-");
        executor.initialize();
        return executor;
    }

    /**
     * Executor thread pool for HTTP performance tests calls with size 40
     * @return Executor Object
     */
    @Bean(name = "taskExecutorPerformance")
    public Executor taskExecutorPerformance() {
        LOGGER.debug("Creating Async Task Executor");
        final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(20);
        executor.setMaxPoolSize(40);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("PerformanceThread-");
        executor.initialize();
        return executor;
    }
}
