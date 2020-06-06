package com.honeybadgers.realtimescheduler.services;

import com.honeybadgers.realtimescheduler.model.RedisTask;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ISchedulerService {

    RedisTask createRedisTask(String taskId);

    boolean checkTaskOnLocked(String taskId);

    void scheduleTask(String taskId);

    List<RedisTask> getAllRedisTasksAndSort();
}