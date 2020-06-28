package com.honeybadgers.cleaner.services;

import com.honeybadgers.cleaner.repository.LockRedisRepository;
import com.honeybadgers.communication.ICommunication;
import com.honeybadgers.models.model.RedisLock;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Collection;

import static com.honeybadgers.models.model.Constants.LOCK_GROUP_PREFIX_RUNNING_TASKS;

@Component
public class ScheduledServices {

    static final Logger logger = LogManager.getLogger(ScheduledServices.class);

    @Autowired
    PausedRepository pausedRepository;

    @Autowired
    ICommunication sender;

    @Value("${scheduler.trigger}")
    String scheduler_trigger;

    @Scheduled(fixedRateString = "${cleaner.paused.fixed-rate}", initialDelayString = "${cleaner.paused.initial-delay}")
    public void cleanPausedLocks() {
        // TODO: for optimisation write custom query which gets all where resume_date != null
        try {
            boolean found = false;
            logger.info("Starting paused cleanup!");
            List<Paused> paused = pausedRepository.findAll();
            logger.info("Checking " + paused.size() + " locks on resume_date");
            for (Paused pause : paused) {
                if(pause.getResumeDate() == null)
                    continue;

                logger.info("resume date is at: " + pause.getResumeDate().toString());
                Timestamp now = Timestamp.from(Instant.now());
                logger.info("current time is at: " + now.toString());
                if(pause.getResumeDate().before(now)) {
                    logger.info("Deleting lock with id " + pause.getId());
                    pausedRepository.deleteById(pause.getId());
                    found = true;
                }
            }
            logger.info("Finished paused cleanup!");
            if(found)
                sender.sendTaskToTasksQueue(scheduler_trigger);

        } catch (Exception e) {
            logger.error("Caught exception in cleanPausedLocks:");
            logger.error(Arrays.deepToString(e.getStackTrace()));
        }
    }
}
