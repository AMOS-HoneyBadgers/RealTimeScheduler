package com.honeybadgers.realtimescheduler.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;


public class SchedulerService {

    private static Logger logger = LoggerFactory.getLogger(SchedulerService.class);

    @Scheduled(fixedDelay = 1000)
    public void scheduleFixedDelayTask(){
        logger.info("worked");
    }
}
