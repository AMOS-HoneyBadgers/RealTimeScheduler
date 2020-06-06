package com.honeybadgers.managementapi.service;

import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public interface IManagementService {
    void pauseScheduler();
    void resumeScheduler();
    void pauseTask(UUID task, OffsetDateTime resumeDate);
    void resumeTask(UUID task);

}
