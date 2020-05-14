package com.honeybadgers.realtimescheduler.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import redis.embedded.RedisServer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;

@TestConfiguration
public class RedisTestConfig {

    private RedisServer redisServer;

    private int redisPort;
    private String redisHost;

    public RedisTestConfig(
            @Value("${spring.redis.port}") int redisPort,
            @Value("${spring.redis.host}") String redisHost) {
        this.redisPort = redisPort;
        this.redisHost = redisHost;
        try {
            this.redisServer = new RedisServer(redisPort);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @PostConstruct
    public void postConstruct() {
        redisServer.start();
    }

    @PreDestroy
    public void preDestroy() {
        redisServer.stop();
    }
}
