package com.honeybadgers.managementapi.service;

import com.honeybadgers.managementapi.exception.LockException;
import com.honeybadgers.models.exceptions.TransactionRetriesExceeded;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
public interface IManagementService {

    /**
     * Pauses the Scheduler. Scheduler does not dispatch Tasks anymore.
     * Scheduler still is able to receive and schedule the Tasks.
     * @param resumeDate Date when the Scheduler automatically resumes working or null.
     * @throws LockException Scheduler is already paused.
     * @throws InterruptedException Thread.sleep error for retry
     * @throws TransactionRetriesExceeded if transaction has failed n times (configurable in application.properties)
     */
    void pauseScheduler(OffsetDateTime resumeDate) throws LockException, InterruptedException, TransactionRetriesExceeded;

    /**
     * Resumes the Scheduler.
     * @throws LockException Scheduler was not paused.
     * @throws InterruptedException Thread.sleep error for retry
     * @throws TransactionRetriesExceeded if transaction has failed n times (configurable in application.properties)
     */
    void resumeScheduler() throws InterruptedException, TransactionRetriesExceeded, LockException;

    /**
     * Pauses a Task, preventing it from being dispatched.
     * @param taskId id of specified Task.
     * @param resumeDate Date when Task automatically resumes or null.
     * @throws LockException Task is already paused.
     * @throws InterruptedException Thread.sleep error for retry
     * @throws TransactionRetriesExceeded if transaction has failed n times (configurable in application.properties)
     */
    void pauseTask(String taskId, OffsetDateTime resumeDate) throws LockException, InterruptedException, TransactionRetriesExceeded;

    /**
     * Resumes a Task.
     * @param taskId id of specified Task.
     * @throws LockException Task was not paused.
     * @throws InterruptedException Thread.sleep error for retry
     * @throws TransactionRetriesExceeded if transaction has failed n times (configurable in application.properties)
     */
    void resumeTask(String taskId) throws InterruptedException, TransactionRetriesExceeded, LockException;

    /**
     * Pauses a Group.
     * @param groupId id of specified Group.
     * @param resumeDate Date when Group automatically resumes or null.
     * @throws LockException Group is already paused.
     * @throws InterruptedException Thread.sleep error for retry
     * @throws TransactionRetriesExceeded if transaction has failed n times (configurable in application.properties)
     */
    void pauseGroup(String groupId, OffsetDateTime resumeDate) throws LockException, InterruptedException, TransactionRetriesExceeded;

    /**
     * Resumes a Group.
     * @param groupId id of specified Group.
     * @throws LockException Group was not paused.
     * @throws InterruptedException Thread.sleep error for retry
     * @throws TransactionRetriesExceeded if transaction has failed n times (configurable in application.properties)
     */
    void resumeGroup(String groupId) throws InterruptedException, TransactionRetriesExceeded, LockException;
}
