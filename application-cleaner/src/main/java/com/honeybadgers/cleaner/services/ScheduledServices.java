package com.honeybadgers.cleaner.services;

import com.honeybadgers.communication.ICommunication;
import com.honeybadgers.models.model.Paused;
import com.honeybadgers.postgre.repository.PausedRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.TransactionException;
import org.hibernate.exception.LockAcquisitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Component
public class ScheduledServices {

    static final Logger logger = LogManager.getLogger(ScheduledServices.class);

    @Autowired
    PausedRepository pausedRepository;

    @Autowired
    ICommunication sender;

    @Value("${scheduler.trigger}")
    String scheduler_trigger;

    /**
     * This Component check for paused Tasks and Groups and makes sure they are unlocked at specified times.
     */
    @Scheduled(fixedRateString = "${cleaner.paused.fixed-rate}", initialDelayString = "${cleaner.paused.initial-delay}")
    public void cleanPausedLocks() {
        logger.info("Cleaner starting paused cleanup");
        List<Paused> deleted = new ArrayList<>();
        try {
            deleted = pausedRepository.deleteAllExpired();
        } catch (JpaSystemException | TransactionException | CannotAcquireLockException | LockAcquisitionException exception) {
            logger.warn("Cleaner encountered transaction exception!");
        }
        logger.info("Cleaner finished paused cleanup - deleted " + deleted.size() + " elements");
        if(deleted.size() > 0) {
            sender.sendTaskToTasksQueue(scheduler_trigger);
            logger.info("Notified scheduler for rescheduling!");
        }
    }
}
