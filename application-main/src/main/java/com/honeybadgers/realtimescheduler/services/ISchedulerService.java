package com.honeybadgers.realtimescheduler.services;

import com.honeybadgers.models.model.RedisTask;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ISchedulerService {

    RedisTask createRedisTask(String taskId);

    boolean isTaskLocked(String taskId);

    boolean isGroupLocked(String groupId);

    boolean isSchedulerLocked();

    void scheduleTask(String taskId);

    List<RedisTask> getAllRedisTasksAndSort();
}
