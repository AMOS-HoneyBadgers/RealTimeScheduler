package com.honeybadgers.realtimescheduler.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public interface ISchedulerService {

    boolean isTaskLocked(String taskId);

    boolean isGroupLocked(String groupId);

    boolean isSchedulerLocked();

    @Transactional(isolation = Isolation.SERIALIZABLE)
    void scheduleTask();
}
