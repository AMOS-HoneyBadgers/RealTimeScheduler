package com.honeybadgers.realtimescheduler.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@TestConfiguration
@EntityScan(basePackages = {"com.honeybadgers.models"})
@EnableJpaRepositories(basePackages = {"com.honeybadgers.realtimescheduler.repository"}) // enable all jpa repositories in the given paths
@EnableAutoConfiguration
public class PostgreTestConfig {
}
