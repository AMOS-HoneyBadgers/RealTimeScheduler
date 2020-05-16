package com.honeybadgers.realtimescheduler.config;

import com.honeybadgers.realtimescheduler.domain.jpa.Task;
import com.honeybadgers.realtimescheduler.repository.TaskRedisRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@Profile("redis")
public class RedisConfig {

    /*NEEDS MVN DEP FOR JEDIS @Bean
    JedisConnectionFactory jedisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration("localhost", 6379);
        return new JedisConnectionFactory(config);
    }*/
    @Bean
    public LettuceConnectionFactory lettuceConnectionFactory() {

        return new LettuceConnectionFactory(new RedisStandaloneConfiguration("localhost", 6379));
    }

    /*@Bean
    public TaskRedisRepository redisRepository(RedisTemplate<String, Task> redisTemplate) {
        return new TaskRedisRepository(redisTemplate);
    }*/

    @Bean
    public RedisTemplate<String, Task> redisTemplate(/*RedisConnectionFactory redisConnectionFactory*/) {
        RedisTemplate<String, Task> template = new RedisTemplate<>();

        template.setConnectionFactory(lettuceConnectionFactory());
        /*template.setConnectionFactory(redisConnectionFactory);

        RedisSerializer<String> stringSerializer = new StringRedisSerializer();
        RedisSerializer<Task> albumSerializer = new Jackson2JsonRedisSerializer<>(Task.class);

        template.setKeySerializer(stringSerializer);
        template.setValueSerializer(albumSerializer);
        template.setHashKeySerializer(stringSerializer);
        template.setHashValueSerializer(albumSerializer);*/

        return template;
    }

}
