package com.honeybadgers.cleaner.services;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@ComponentScan(basePackages = "com.honeybadgers.cleaner.services")
public class ScheduledConfigTest {
}
