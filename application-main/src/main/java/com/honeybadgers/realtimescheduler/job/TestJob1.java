package com.honeybadgers.realtimescheduler.job;


import com.honeybadgers.realtimescheduler.services.TaskService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

public class TestJob1 implements Job {

    @Autowired
    private TaskService taskService;
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        try {
            String id = jobExecutionContext.getJobDetail().getKey().getName();
            taskService.run(id);
        } catch (Exception e) {
            throw new JobExecutionException(e);
        }
    }
}
