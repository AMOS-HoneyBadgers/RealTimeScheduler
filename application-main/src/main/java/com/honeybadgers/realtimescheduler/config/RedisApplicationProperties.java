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
@ConfigurationProperties("vcap.services.redis.credentials")
public class RedisApplicationProperties {

    String redis_lock_host;
    String redis_lock_pw;
    String redis_lock_port;

    String redis_prio_host;
    String redis_prio_pw;
    String redis_prio_port;

    @Override
    public String toString() {
        return "RedisApplicationProperties{" +
                "redis_lock_host='" + redis_lock_host + '\'' +
                ", redis_lock_pw='" + redis_lock_pw + '\'' +
                ", redis_lock_port='" + redis_lock_port + '\'' +
                ", redis_prio_host='" + redis_prio_host + '\'' +
                ", redis_prio_pw='" + redis_prio_pw + '\'' +
                ", redis_prio_port='" + redis_prio_port + '\'' +
                '}';
    }
}
