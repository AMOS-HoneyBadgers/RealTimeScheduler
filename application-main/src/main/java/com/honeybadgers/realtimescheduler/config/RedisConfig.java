package com.honeybadgers.realtimescheduler.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@Configuration
@EnableRedisRepositories(basePackages = "com.honeybadgers.realtimescheduler")
public class RedisConfig {

    @Value("${spring.redis.prio.host}")
    private String priohost;

    @Value("${spring.redis.prio.port}")
    private int prioport;

    @Value("${spring.redis.prio.password}")
    private String priopassword;

    @Value("${spring.redis.lock.host}")
    private String lockhost;

    @Value("${spring.redis.lock.port}")
    private int lockport;

    @Value("${spring.redis.lock.password}")
    private String lockpassword;


    @Bean(name="prioConnectionFactory")
    JedisConnectionFactory jedisConnectionFactoryForPrioDatabase() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(priohost);
        redisStandaloneConfiguration.setPassword(priopassword);
        redisStandaloneConfiguration.setPort(prioport);
        return new JedisConnectionFactory(redisStandaloneConfiguration);
    }


    @Bean(name="lockConnectionFactory")
    JedisConnectionFactory jedisConnectionFactoryForLockDatabase() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(lockhost);
        redisStandaloneConfiguration.setPassword(lockpassword);
        redisStandaloneConfiguration.setPort(lockport);
        return new JedisConnectionFactory(redisStandaloneConfiguration);
    }

    @Bean(name="prioRedisTemplate")
    @Primary
    public RedisTemplate<String, Object> prioRedisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactoryForPrioDatabase());
        return template;
    }

    @Bean(name="lockRedisTemplate")
    public RedisTemplate<String, Object> lockRedisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactoryForLockDatabase());
        return template;
    }





}
