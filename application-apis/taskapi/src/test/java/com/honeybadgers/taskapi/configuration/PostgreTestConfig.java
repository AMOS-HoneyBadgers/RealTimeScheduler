package com.honeybadgers.taskapi.configuration;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Configuration;

@TestConfiguration
@EntityScan(basePackages = {"com.honeybadgers.models"})
//@EnableJpaRepositories(basePackages = {"com.honeybadgers.groupapi.repository"}) // enable all jpa repositories in the given paths
@EnableAutoConfiguration
public class PostgreTestConfig {
}
