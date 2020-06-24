package com.honeybadgers.clienttests;


import com.honeybadgers.clienttests.performance.PerformanceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.web.client.RestTemplateBuilder;


@SpringBootApplication(scanBasePackages = "com.honeybadgers", exclude = DataSourceAutoConfiguration.class)
@Slf4j
public class ClientTestsApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClientTestsApplication.class, args);
    }
}
