package com.honeybadgers.cleaner.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Configuration
@ConfigurationProperties("vcap.services.redis.credentials")
public class RedisApplicationProperties {

    String redis_lock_host;
    String redis_lock_pw;
    String redis_lock_port = "0";           // To prevent NullPointerException when Integer.parseInt()

    @Override
    public String toString() {
        return "RedisApplicationProperties{" +
                "redis_lock_host='" + redis_lock_host + '\'' +
                ", redis_lock_pw='" + redis_lock_pw + '\'' +
                ", redis_lock_port='" + redis_lock_port + '\'' +
                '}';
    }
}