package com.honeybadgers.taskapi.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan(basePackages = {"com.honeybadgers.models"})
@EnableJpaRepositories(basePackages = {"com.honeybadgers.taskapi.repository"}) // enable all jpa repositories in the given paths
@EnableAutoConfiguration
public class PostgreConfig {
}
