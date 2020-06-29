package com.honeybadgers.managementapi.service;

import com.honeybadgers.managementapi.exception.LockException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public interface IManagementService {

    @Transactional(isolation = Isolation.SERIALIZABLE)
    void pauseScheduler(OffsetDateTime resumeDate) throws LockException;

    void resumeScheduler();

    @Transactional(isolation = Isolation.SERIALIZABLE)
    void pauseTask(UUID taskId, OffsetDateTime resumeDate) throws LockException;

    void resumeTask(UUID taskId);

    @Transactional(isolation = Isolation.SERIALIZABLE)
    void pauseGroup(String groupId, OffsetDateTime resumeDate) throws LockException;

    void resumeGroup(String grouId);
}
