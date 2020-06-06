package com.honeybadgers.managementapi.service;

import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
public interface IManagementService {
    public void pauseScheduler();
    public void resumeScheduler();
    public void pauseTask(Long task, OffsetDateTime resumeDate);
    public void resumeTask(Long task);

}
