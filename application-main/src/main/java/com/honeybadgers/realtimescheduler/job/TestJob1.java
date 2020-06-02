package com.honeybadgers.realtimescheduler.job;


import com.honeybadgers.realtimescheduler.services.impl.TaskService;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

public class TestJob1 implements Job {

    @Autowired
    private TaskService taskService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        try {
            String id = jobExecutionContext.getJobDetail().getJobDataMap().getString("id");
            //taskService.run(id);
            System.out.println("started Job ID: " + id + "Date: " + new Date());
        } catch (Exception e) {
            throw new JobExecutionException(e);
        }
    }
}
