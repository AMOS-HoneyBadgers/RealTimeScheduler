package com.honeybadgers.managementapi.service.impl;

import com.honeybadgers.communication.ICommunication;
import com.honeybadgers.managementapi.exception.PauseException;
import com.honeybadgers.managementapi.service.IManagementService;
import com.honeybadgers.models.exceptions.TransactionRetriesExceeded;
import com.honeybadgers.models.model.Paused;
import com.honeybadgers.postgre.repository.PausedRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.TransactionException;
import org.hibernate.exception.LockAcquisitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static com.honeybadgers.models.model.Constants.*;

@Service
public class ManagementService implements IManagementService {

    static final Logger logger = LogManager.getLogger(ManagementService.class);

    @Value("${scheduler.trigger}")
    String scheduler_trigger;

    @Autowired
    PausedRepository pausedRepository;

    @Autowired
    ICommunication sender;

    @Value("${com.honeybadgers.transaction.max-retry-sleep:500}")
    int maxTransactionRetrySleep;

    @Value("${com.honeybadgers.transaction.max-retry-count:5}")
    int maxTransactionRetryCount;

    @Override
    public void pauseScheduler(OffsetDateTime resumeDate) throws PauseException, InterruptedException, TransactionRetriesExceeded {
        int iteration = 1;
        while(iteration <= maxTransactionRetryCount) {
            Paused toSave = new Paused();
            toSave.setId(PAUSED_SCHEDULER_ALIAS);
            if(resumeDate != null)
                toSave.setResumeDate(Timestamp.valueOf(LocalDateTime.ofEpochSecond(resumeDate.toEpochSecond(), 0, ZoneOffset.UTC)));

            try {
                pausedRepository.insertCustomQuery(toSave.getId(), toSave.getResumeDate());
                return;
            } catch (DataIntegrityViolationException e) {
                if(e.getMessage() != null && e.getMessage().contains("constraint [paused_pkey]")) {
                    throw new PauseException("Already locked!");
                }
                throw e;
            } catch (JpaSystemException | TransactionException | CannotAcquireLockException | LockAcquisitionException exception){
                // TransactionException is nested ex of JpaSystemException and LockAcquisitionException is nested of CannotAcquireLockException
                double timeToSleep= Math.random()*maxTransactionRetrySleep*iteration;
                logger.warn("Transaction exception while pausing scheduler. Try again after "+timeToSleep+" milliseconds" );
                Thread.sleep(Math.round(timeToSleep));
                iteration++;
            }
        }
        // throw exception due to surpassing max retries
        throw new TransactionRetriesExceeded("Failed transaction " + maxTransactionRetryCount + " times!");
    }

    @Override
    public void resumeScheduler() throws InterruptedException, TransactionRetriesExceeded, PauseException {
        int iteration = 1;
        while(iteration <= maxTransactionRetryCount) {
            try {
                Paused paused = pausedRepository.deleteByIdCustomQuery(PAUSED_SCHEDULER_ALIAS).orElse(null);
                if(paused == null)
                    throw new PauseException("Already unlocked!");
                // only trigger reschedule if change in paused entities
                sender.sendTaskToTasksQueue("123");
                return;
            } catch (JpaSystemException | TransactionException | CannotAcquireLockException | LockAcquisitionException exception){
                // TransactionException is nested ex of JpaSystemException and LockAcquisitionException is nested of CannotAcquireLockException
                double timeToSleep= Math.random()*maxTransactionRetrySleep*iteration;
                logger.warn("Transaction exception while resuming scheduler. Try again after "+timeToSleep+" milliseconds" );
                Thread.sleep(Math.round(timeToSleep));
                iteration++;
            }
        }
        // throw exception due to surpassing max retries
        throw new TransactionRetriesExceeded("Failed transaction " + maxTransactionRetryCount + " times!");
    }

    @Override
    public void pauseTask(String taskId, OffsetDateTime resumeDate) throws PauseException, InterruptedException, TransactionRetriesExceeded {
        int iteration = 1;
        while(iteration <= maxTransactionRetryCount) {
            String id = PAUSED_TASK_PREFIX + taskId;
            Paused toSave = new Paused();
            toSave.setId(id);
            if(resumeDate != null)
                toSave.setResumeDate(Timestamp.valueOf(LocalDateTime.ofEpochSecond(resumeDate.toEpochSecond(), 0, ZoneOffset.UTC)));

            try {
                pausedRepository.insertCustomQuery(toSave.getId(), toSave.getResumeDate());
                return;
            } catch (DataIntegrityViolationException e) {
                if(e.getMessage() != null && e.getMessage().contains("constraint [paused_pkey]")) {
                    throw new PauseException("Already locked!");
                }
                throw e;
            } catch (JpaSystemException | TransactionException | CannotAcquireLockException | LockAcquisitionException exception){
                // TransactionException is nested ex of JpaSystemException and LockAcquisitionException is nested of CannotAcquireLockException
                double timeToSleep= Math.random()*maxTransactionRetrySleep*iteration;
                logger.warn("Transaction exception while pausing task. Try again after "+timeToSleep+" milliseconds" );
                Thread.sleep(Math.round(timeToSleep));
                iteration++;
            }
        }
        // throw exception due to surpassing max retries
        throw new TransactionRetriesExceeded("Failed transaction " + maxTransactionRetryCount + " times!");
    }

    @Override
    public void resumeTask(String taskId) throws InterruptedException, TransactionRetriesExceeded, PauseException {
        int iteration = 1;
        while(iteration <= maxTransactionRetryCount) {
            try {
                String id = PAUSED_TASK_PREFIX + taskId;
                Paused paused = pausedRepository.deleteByIdCustomQuery(id).orElse(null);
                if(paused == null)
                    throw new PauseException("Already unlocked!");
                // only trigger reschedule if change in paused entities
                sender.sendTaskToTasksQueue("123");
                return;
            } catch (JpaSystemException | TransactionException | CannotAcquireLockException | LockAcquisitionException exception){
                // TransactionException is nested ex of JpaSystemException and LockAcquisitionException is nested of CannotAcquireLockException
                double timeToSleep= Math.random()*maxTransactionRetrySleep*iteration;
                logger.warn("Transaction exception while resuming task. Try again after "+timeToSleep+" milliseconds" );
                Thread.sleep(Math.round(timeToSleep));
                iteration++;
            }
        }
        // throw exception due to surpassing max retries
        throw new TransactionRetriesExceeded("Failed transaction " + maxTransactionRetryCount + " times!");
    }

    @Override
    public void pauseGroup(String groupId, OffsetDateTime resumeDate) throws PauseException, InterruptedException, TransactionRetriesExceeded {
        int iteration = 1;
        while(iteration <= maxTransactionRetryCount) {
            String id = PAUSED_GROUP_PREFIX + groupId;
            Paused toSave = new Paused();
            toSave.setId(id);
            if(resumeDate != null)
                toSave.setResumeDate(Timestamp.valueOf(LocalDateTime.ofEpochSecond(resumeDate.toEpochSecond(), 0, ZoneOffset.UTC)));

            try {
                pausedRepository.insertCustomQuery(toSave.getId(), toSave.getResumeDate());
                return;
            } catch (DataIntegrityViolationException e) {
                if(e.getMessage() != null && e.getMessage().contains("constraint [paused_pkey]")) {
                    throw new PauseException("Already locked!");
                }
                throw e;
            } catch (JpaSystemException | TransactionException | CannotAcquireLockException | LockAcquisitionException exception){
                // TransactionException is nested ex of JpaSystemException and LockAcquisitionException is nested of CannotAcquireLockException
                double timeToSleep= Math.random()*maxTransactionRetrySleep*iteration;
                logger.warn("Transaction exception while pausing group. Try again after "+timeToSleep+" milliseconds" );
                Thread.sleep(Math.round(timeToSleep));
                iteration++;
            }
        }
        // throw exception due to surpassing max retries
        throw new TransactionRetriesExceeded("Failed transaction " + maxTransactionRetryCount + " times!");
    }

    @Override
    public void resumeGroup(String groupId) throws InterruptedException, TransactionRetriesExceeded, PauseException {
        int iteration = 1;
        while(iteration <= maxTransactionRetryCount) {
            try {
                String id = PAUSED_GROUP_PREFIX + groupId;
                Paused paused = pausedRepository.deleteByIdCustomQuery(id).orElse(null);
                if(paused == null)
                    throw new PauseException("Already unlocked!");
                // only trigger reschedule if change in paused entities
                sender.sendTaskToTasksQueue("123");
                return;
            } catch (JpaSystemException | TransactionException | CannotAcquireLockException | LockAcquisitionException exception){
                // TransactionException is nested ex of JpaSystemException and LockAcquisitionException is nested of CannotAcquireLockException
                double timeToSleep= Math.random()*maxTransactionRetrySleep*iteration;
                logger.warn("Transaction exception while resuming group. Try again after "+timeToSleep+" milliseconds" );
                Thread.sleep(Math.round(timeToSleep));
                iteration++;
            }
        }
        // throw exception due to surpassing max retries
        throw new TransactionRetriesExceeded("Failed transaction " + maxTransactionRetryCount + " times!");
    }
}
