package com.honeybadgers.realtimescheduler.config;


import com.honeybadgers.realtimescheduler.domain.jpa.Task;
import com.honeybadgers.realtimescheduler.job.TaskJob;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.util.Date;

import static org.quartz.DateBuilder.futureDate;


//@EnableScheduling
@Configuration
public class ScheduleConfig {

    SchedulerFactory schedulerFactory = new StdSchedulerFactory();
    Scheduler scheduler = schedulerFactory.getScheduler();
    //scheduler.start();

    public ScheduleConfig() throws SchedulerException, SchedulerException {
        System.out.println("Scheduler not started --- Exception");
    }

    Date startTime = futureDate(5, DateBuilder.IntervalUnit.SECOND);

    @Bean
    public JobDetail job(){
        return JobBuilder.newJob(TaskJob.class)
                .withIdentity("myJob", "group1")
                .usingJobData("jobSays", "Hello World!")
                .usingJobData("myFloatValue", 3.141f)
                .storeDurably(true)
                .build();
    }
    @Bean
    public JobDetail jobB(){
        return JobBuilder.newJob(TaskJob.class)
                .withIdentity("myJob", "group2")
                .usingJobData("jobSays", "Hello World!&7869769")
                .usingJobData("myFloatValue", 3.141f)
                .storeDurably(true)
                .build();
    }

    @Bean
    public Trigger trigger(){
        return TriggerBuilder.newTrigger()
                .forJob(job())
                .withIdentity("myTrigger", "group1")
                .startAt(startTime)
                .withPriority(10)
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInSeconds(3)
                        .repeatForever())
                .build();
    }

    @Bean
    public Trigger triggerB(){
        return TriggerBuilder.newTrigger()
                .forJob(jobB())
                .withIdentity("myTrigger2", "group2")
                .startAt(startTime)
                .withPriority(15)
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInSeconds(3)
                        .repeatForever())
                .build();
    }
}
