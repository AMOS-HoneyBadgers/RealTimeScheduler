package com.honeybadgers.clienttests;


import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.honeybadgers")
@Slf4j
public class ClientTestsApplication {
    public static void main(String[] args) {
        SpringApplication.run(ClientTestsApplication.class, args);
    }

}
