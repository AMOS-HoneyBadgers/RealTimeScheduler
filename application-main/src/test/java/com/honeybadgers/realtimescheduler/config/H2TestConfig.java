package com.honeybadgers.realtimescheduler.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EntityScan(basePackages = {"com.honeybadgers.models", "com.honeybadgers.realtimescheduler.model"})
@PropertySource("classpath:application-test.properties")
@EnableTransactionManagement
public class H2TestConfig {
}
