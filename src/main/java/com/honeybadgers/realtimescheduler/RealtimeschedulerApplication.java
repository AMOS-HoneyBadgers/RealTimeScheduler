package com.honeybadgers.realtimescheduler;

import com.honeybadgers.realtimescheduler.job.TaskJob;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RealtimeschedulerApplication {

    public static void main(String[] args) throws SchedulerException {
        SpringApplication.run(RealtimeschedulerApplication.class, args);
    }

}
