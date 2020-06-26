package com.honeybadgers.cleaner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.honeybadgers")
public class CleanerApplication {

    public static void main(String[] args) {
        SpringApplication.run(CleanerApplication.class, args);
    }

}
