package com.honeybadgers.realtimescheduler.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Configuration
@ConfigurationProperties("spring.redis")
public class RedisApplicationProperties {

    RedisProperties prio;

    RedisProperties lock;

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class RedisProperties {

        String host;

        String password;

        String port;
    }
}
