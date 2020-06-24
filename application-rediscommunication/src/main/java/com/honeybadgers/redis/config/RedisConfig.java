package com.honeybadgers.redis.config;

import com.honeybadgers.models.model.RedisLock;
import com.honeybadgers.models.model.RedisTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableRedisRepositories(basePackages = "com.honeybadgers.redis")
public class RedisConfig {

    @Autowired
    RedisApplicationProperties redisApplicationProperties;


    @Bean(name="prioConnectionFactory")
    @Primary
    JedisConnectionFactory jedisConnectionFactoryForPrioDatabase() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(redisApplicationProperties.redis_prio_host);
        redisStandaloneConfiguration.setPassword(redisApplicationProperties.redis_prio_pw);
        redisStandaloneConfiguration.setPort(Integer.parseInt(redisApplicationProperties.redis_prio_port));
        return new JedisConnectionFactory(redisStandaloneConfiguration);
    }


    @Bean(name="lockConnectionFactory")
    JedisConnectionFactory jedisConnectionFactoryForLockDatabase() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(redisApplicationProperties.redis_lock_host);
        redisStandaloneConfiguration.setPassword(redisApplicationProperties.redis_lock_pw);
        redisStandaloneConfiguration.setPort(Integer.parseInt(redisApplicationProperties.redis_lock_port));
        return new JedisConnectionFactory(redisStandaloneConfiguration);
    }

    @Bean(name="prioRedisTemplate")
    @Primary
    public RedisTemplate<String, RedisTask> prioRedisTemplate() {
        RedisTemplate<String, RedisTask> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactoryForPrioDatabase());
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }

    @Bean(name="lockRedisTemplate")
    public RedisTemplate<String, RedisLock> lockRedisTemplate() {
        RedisTemplate<String, RedisLock> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactoryForLockDatabase());
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }

}
