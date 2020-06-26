package com.honeybadgers.realtimescheduler.services;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ISchedulerService {

    boolean isTaskLocked(String taskId);

    boolean isGroupLocked(String groupId);

    boolean isSchedulerLocked();

    void scheduleTask(String taskId);
}
