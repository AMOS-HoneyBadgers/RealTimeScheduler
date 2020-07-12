package com.honeybadgers.cleaner.services;

import com.honeybadgers.communication.ICommunication;
import com.honeybadgers.models.model.Paused;
import com.honeybadgers.postgre.repository.PausedRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.Instant;
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
        List<Paused> deleted = pausedRepository.deleteAllExpired();
        logger.info("Cleaner finished paused cleanup - deleted " + deleted.size() + " elements");
        if(deleted.size() > 0) {
            sender.sendTaskToTasksQueue(scheduler_trigger);
            logger.info("Notified scheduler for rescheduling!");
        }
    }


}
