package com.honeybadgers.groupapi.configuration;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.TestConfiguration;

@TestConfiguration
@EntityScan(basePackages = {"com.honeybadgers.models"})
@EnableAutoConfiguration
public class PostgreTestConfig {
}
