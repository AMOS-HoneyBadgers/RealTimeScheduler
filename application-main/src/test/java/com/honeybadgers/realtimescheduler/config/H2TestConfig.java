package com.honeybadgers.realtimescheduler.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EntityScan(basePackages = {"com.honeybadgers.models", "com.honeybadgers.realtimescheduler.model"})
//@EnableJpaRepositories(basePackages = {"com.honeybadgers.realtimescheduler.repository"}) // enable all jpa repositories in the given paths
@PropertySource("classpath:application-test.properties")
@EnableTransactionManagement
public class H2TestConfig {
}
