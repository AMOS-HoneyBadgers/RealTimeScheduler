package com.honeybadgers.realtimescheduler.config;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzConfig {

    /*@Bean
    public Scheduler scheduler() throws SchedulerException {
        SchedulerFactory schedulerFactory = new StdSchedulerFactory();
        Scheduler scheduler = schedulerFactory.getScheduler();
        scheduler.start();
        return scheduler;
    }*/
    //@Bean
    //@QuartzDataSource
    //public DataSource quartzDataSource() {
    //    return DataSourceBuilder.create().build();
    //}
}
