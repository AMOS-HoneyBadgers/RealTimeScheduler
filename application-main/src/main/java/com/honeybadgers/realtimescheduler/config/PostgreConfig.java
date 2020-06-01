package com.honeybadgers.realtimescheduler.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan(basePackages = {"com.honeybadgers.models"})
@EnableJpaRepositories(basePackages = {"com.honeybadgers.realtimescheduler.repository"}) // enable all jpa repositories in the given paths
@EnableAutoConfiguration
public class PostgreConfig {
}
