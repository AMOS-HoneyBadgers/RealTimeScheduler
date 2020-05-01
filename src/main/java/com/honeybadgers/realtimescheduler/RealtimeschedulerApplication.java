package com.honeybadgers.realtimescheduler;

import com.honeybadgers.realtimescheduler.services.PostgresExampleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
@Slf4j
public class RealtimeschedulerApplication {

    @Autowired
    PostgresExampleService postgresExampleService;

    /*@Autowired
    RedisExampleService redisExampleService;*/

    public static void main(String[] args) {
        SpringApplication.run(RealtimeschedulerApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void printUsageInformations() {
        log.info("====================== Example Executions ======================");
        postgresExampleService.printMaxPrio();
        postgresExampleService.printMinPrio();
        //redisExampleService.testRedis();
    }
}
