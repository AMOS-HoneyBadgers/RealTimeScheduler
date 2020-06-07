package com.honeybadgers.managementapi.service;

import com.honeybadgers.managementapi.exception.LockException;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public interface IManagementService {
    void pauseScheduler(OffsetDateTime resumeDate) throws LockException;
    void resumeScheduler();
    void pauseTask(UUID taskId, OffsetDateTime resumeDate) throws LockException;
    void resumeTask(UUID taskId);
    void pauseGroup(String groupId, OffsetDateTime resumeDate) throws LockException;
    void resumeGroup(String grouId);

}
