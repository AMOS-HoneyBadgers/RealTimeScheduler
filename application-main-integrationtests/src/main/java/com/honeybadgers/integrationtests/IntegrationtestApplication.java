package com.honeybadgers.integrationtests;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.honeybadgers")
@Slf4j
public class IntegrationtestApplication {
    public static void main(String[] args) {
        SpringApplication.run(IntegrationtestApplication.class, args);
    }
}
