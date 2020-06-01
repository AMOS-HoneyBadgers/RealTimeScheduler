package com.honeybadgers.taskapi.controllers;

import com.honeybadgers.taskapi.configuration.PostgreConfig;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.TestConfiguration;


@TestConfiguration
@SpringBootApplication(exclude = {
        PostgreConfig.class         // exclude PostgreConfig to prevent Jpa initialization
})

public class TestConfig {

}
