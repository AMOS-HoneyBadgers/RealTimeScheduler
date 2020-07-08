package com.honeybadgers.managementapi.service;

import com.honeybadgers.managementapi.exception.LockException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public interface IManagementService {

    /**
     * Pauses the Scheduler. Scheduler does not dispatch Tasks anymore.
     * Scheduler still is able to receive and schedule the Tasks.
     * @param resumeDate Date when the Scheduler automatically resumes working or null.
     * @throws LockException Scheduler is already locked.
     */
    @Transactional(isolation = Isolation.SERIALIZABLE)
    void pauseScheduler(OffsetDateTime resumeDate) throws LockException;

    /**
     * Resumes the Scheduler.
     */
    void resumeScheduler();

    /**
     * Locks a Task, preventing it from being dispatched.
     * @param taskId id of specified Task.
     * @param resumeDate Date when Task is unlocked automatically or null.
     * @throws LockException Task is already locked.
     */
    @Transactional(isolation = Isolation.SERIALIZABLE)
    void pauseTask(String taskId, OffsetDateTime resumeDate) throws LockException;

    /**
     * Unlocks a Task.
     * @param taskId id of specified Task.
     */
    void resumeTask(String taskId);

    /**
     * Locks a Group. Any Tasks under the specified Group in the hierarchy are locked in the process.
     * @param groupId id of specified Group.
     * @param resumeDate Date when Group is unlocked automatically or null.
     * @throws LockException Group is already locked.
     */
    @Transactional(isolation = Isolation.SERIALIZABLE)
    void pauseGroup(String groupId, OffsetDateTime resumeDate) throws LockException;

    /**
     * Unlocks a Group.
     * @param grouId id of specified Group.
     */
    void resumeGroup(String grouId);
}
