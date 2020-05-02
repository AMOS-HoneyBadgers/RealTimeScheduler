package com.honeybadgers.realtimescheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class RealtimeschedulerApplication {

    // these autowired objects were only for test purposes -> normally try to leave this class as less modified as possible

    /*@Autowired
    PostgresExampleService postgresExampleService;

    @Autowired
    RedisExampleService redisExampleService;

    @Autowired
    UserService userService;*/

    public static void main(String[] args) {
        SpringApplication.run(RealtimeschedulerApplication.class, args);
    }

    // this will be executed after the application has started (disabled, because that is bad practice to do this here)
    /*@EventListener(ApplicationReadyEvent.class)
    public void printUsageInformations() {
        log.info("====================== Example Executions ======================");
        userService.test();
    }*/
}
