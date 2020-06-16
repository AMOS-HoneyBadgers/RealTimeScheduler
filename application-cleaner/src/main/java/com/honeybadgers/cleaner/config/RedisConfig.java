package com.honeybadgers.cleaner.config;

import com.honeybadgers.models.RedisLock;
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
import org.springframework.transaction.annotation.EnableTransactionManagement;


@Configuration
@EnableRedisRepositories(basePackages = "com.honeybadgers.cleaner")
public class RedisConfig {

    @Autowired
    RedisApplicationProperties redisApplicationProperties;


    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(redisApplicationProperties.redis_lock_host);
        redisStandaloneConfiguration.setPassword(redisApplicationProperties.redis_lock_pw);
        redisStandaloneConfiguration.setPort(Integer.parseInt(redisApplicationProperties.redis_lock_port));
        return new JedisConnectionFactory(redisStandaloneConfiguration);
    }

    @Bean
    public RedisTemplate<String, RedisLock> redisTemplate() {
        RedisTemplate<String, RedisLock> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        //template.setEnableTransactionSupport(true);

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }

    /* TODO needed for transaction https://docs.spring.io/spring-data/data-redis/docs/current/reference/html/#tx
    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) throws SQLException {
        return new DataSourceTransactionManager(dataSource);
    }*/

}
