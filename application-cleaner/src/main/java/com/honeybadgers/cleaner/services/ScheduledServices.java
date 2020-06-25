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
    LockRedisRepository lockRedisRepository;

    @Autowired
    ICommunication sender;


    @Scheduled(fixedRateString = "${cleaner.paused.fixed-rate}", initialDelayString = "${cleaner.paused.initial-delay}")
    public void cleanPausedLocks() {
        // TODO: for optimisation write custom query which gets all where resume_date != null (REQUIRES COMPLETE IMPL OF CRUDREPOS.)
        try {
            boolean found = false;
            logger.info("Starting paused cleanup!");
            Iterable<RedisLock> paused = lockRedisRepository.findAll();
            logger.info("Checking " + ((Collection<?>) paused).size() + " locks on resume_date");
            for (RedisLock redisLock : paused) {
                if(redisLock.getResume_date() == null)
                    continue;

                // assert, that this is no parallelismObject which has a resume_date by mistake
                if(redisLock.getId().startsWith(LOCK_GROUP_PREFIX_RUNNING_TASKS)) {
                    logger.warn("Found parallelism redisLock object with resume date: " + redisLock.toString());
                    continue;
                }

                logger.info("resume date is at: " + redisLock.getResume_date().toString());
                LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
                logger.info("current time is at: " + now.toString());
                if(redisLock.getResume_date().isBefore(now)) {
                    logger.info("Deleting lock with id " + redisLock.getId());
                    lockRedisRepository.deleteById(redisLock.getId());
                    found = true;
                }
            }
            logger.info("Finished paused cleanup!");
            if(found)

        } catch (Exception e) {
            logger.error("Caught exception in cleanPausedLocks:");
            logger.error(Arrays.deepToString(e.getStackTrace()));
        }
    }
}
