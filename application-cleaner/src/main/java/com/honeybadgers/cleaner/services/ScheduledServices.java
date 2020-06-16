package com.honeybadgers.cleaner.services;

import com.honeybadgers.cleaner.repository.LockRepository;
import com.honeybadgers.models.RedisLock;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Collection;

@Component
public class ScheduledServices {

    static final Logger logger = LogManager.getLogger(ScheduledServices.class);

    public static final String LOCKREDIS_GROUP_PREFIX_RUNNING_TASKS = "GROUP_PREFIX_PARLELLISM_CURRENT_TASKS_RUNNING_FOR_GROUP";

    @Autowired
    LockRepository lockRepository;

    @Scheduled(fixedRateString = "${cleaner.paused.fixed-rate}", initialDelayString = "${cleaner.paused.initial-delay}")
    public void cleanPausedLocks() {
        // TODO: for optimisation write custom query which gets all where resume_date != null (REQUIRES COMPLETE IMPL OF CRUDREPOS.)
        try {
            logger.info("Starting paused cleanup!");
            Iterable<RedisLock> paused = lockRepository.findAll();
            logger.info("Checking " + ((Collection<?>) paused).size() + " locks on resume_date");
            for (RedisLock redisLock : paused) {
                if(redisLock.getResume_date() == null)
                    continue;

                // assert, that this is no parallelismObject which has a resume_date by mistake
                if(redisLock.getId().startsWith(LOCKREDIS_GROUP_PREFIX_RUNNING_TASKS)) {
                    logger.warn("Found parallelism redisLock object with resume date: " + redisLock.toString());
                    continue;
                }

                logger.info("resume date is at: " + redisLock.getResume_date().toString());
                LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
                logger.info("current time is at: " + now.toString());
                if(redisLock.getResume_date().isBefore(now)) {
                    logger.info("Deleting lock with id " + redisLock.getId());
                    lockRepository.delete(redisLock);
                }
            }
            logger.info("Finished paused cleanup!");
        } catch (Exception e) {
            logger.error("Caught exception in cleanPausedLocks:");
            logger.error(Arrays.deepToString(e.getStackTrace()));
        }
    }
}
