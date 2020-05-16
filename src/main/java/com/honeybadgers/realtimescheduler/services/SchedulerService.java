package com.honeybadgers.realtimescheduler.services;

import com.honeybadgers.realtimescheduler.job.TaskJob;
import org.quartz.*;
import org.quartz.impl.StdScheduler;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class SchedulerService {

    //@Autowired
    //private Scheduler scheduler;

    private static Logger logger = LoggerFactory.getLogger(SchedulerService.class);

    public SchedulerService() throws SchedulerException {
    }

    //@Scheduled(fixedDelay = 1000)
    //public void scheduleFixedDelayTask(){
     //   logger.info("worked");
    //}


}
