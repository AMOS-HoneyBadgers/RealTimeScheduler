package com.honeybadgers.realtimescheduler.services;

import com.honeybadgers.realtimescheduler.exception.CreationException;
import com.honeybadgers.realtimescheduler.exception.LimitExceededException;
import com.honeybadgers.realtimescheduler.model.Task;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.springframework.stereotype.Service;

@Service
public interface IQuartzService {

    /*JobDetail createJobDetails(Task task);

    Trigger createJobTrigger(Task task) throws LimitExceededException;

    void scheduleTask(Task task) throws CreationException;

    Long calculateTaskTotalPriority(Task task) throws LimitExceededException;*/
}
