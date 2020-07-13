package com.honeybadgers.managementapi.service;

import com.honeybadgers.managementapi.exception.PauseException;
import com.honeybadgers.models.exceptions.TransactionRetriesExceeded;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
public interface IManagementService {

    /**
     * Pauses the Scheduler. Scheduler does not dispatch Tasks anymore.
     * Scheduler still is able to receive and schedule the Tasks.
     * @param resumeDate Date when the Scheduler automatically resumes working or null.
     * @throws PauseException Scheduler is already paused.
     * @throws InterruptedException Thread.sleep error for retry
     * @throws TransactionRetriesExceeded if transaction has failed n times (configurable in application.properties)
     */
    void pauseScheduler(OffsetDateTime resumeDate) throws PauseException, InterruptedException, TransactionRetriesExceeded;

    /**
     * Resumes the Scheduler.
     * @throws PauseException Scheduler was not paused.
     * @throws InterruptedException Thread.sleep error for retry
     * @throws TransactionRetriesExceeded if transaction has failed n times (configurable in application.properties)
     */
    void resumeScheduler() throws InterruptedException, TransactionRetriesExceeded, PauseException;

    /**
     * Pauses a Task, preventing it from being dispatched.
     * @param taskId id of specified Task.
     * @param resumeDate Date when Task automatically resumes or null.
     * @throws PauseException Task is already paused.
     * @throws InterruptedException Thread.sleep error for retry
     * @throws TransactionRetriesExceeded if transaction has failed n times (configurable in application.properties)
     */
    void pauseTask(String taskId, OffsetDateTime resumeDate) throws PauseException, InterruptedException, TransactionRetriesExceeded;

    /**
     * Resumes a Task.
     * @param taskId id of specified Task.
     * @throws PauseException Task was not paused.
     * @throws InterruptedException Thread.sleep error for retry
     * @throws TransactionRetriesExceeded if transaction has failed n times (configurable in application.properties)
     */
    void resumeTask(String taskId) throws InterruptedException, TransactionRetriesExceeded, PauseException;

    /**
     * Pauses a Group.
     * @param groupId id of specified Group.
     * @param resumeDate Date when Group automatically resumes or null.
     * @throws PauseException Group is already paused.
     * @throws InterruptedException Thread.sleep error for retry
     * @throws TransactionRetriesExceeded if transaction has failed n times (configurable in application.properties)
     */
    void pauseGroup(String groupId, OffsetDateTime resumeDate) throws PauseException, InterruptedException, TransactionRetriesExceeded;

    /**
     * Resumes a Group.
     * @param groupId id of specified Group.
     * @throws PauseException Group was not paused.
     * @throws InterruptedException Thread.sleep error for retry
     * @throws TransactionRetriesExceeded if transaction has failed n times (configurable in application.properties)
     */
    void resumeGroup(String groupId) throws InterruptedException, TransactionRetriesExceeded, PauseException;
}
