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
import java.util.Arrays;
import java.util.Collection;

@Component
public class ScheduledServices {

    static final Logger logger = LogManager.getLogger(ScheduledServices.class);

    @Autowired
    LockRepository lockRepository;

    @Scheduled(fixedRateString = "${cleaner.paused.fixedRate}", initialDelayString = "${cleaner.paused.initialDelay}")
    public void cleanPausedLocks() {
        // TODO: for optimisation write custom query which gets all where resume_date != null (REQUIRES COMPLETE IMPL OF CRUDREPOS.)
        try {
            logger.info("Starting paused cleanup!");
            Iterable<RedisLock> paused = lockRepository.findAll();
            logger.info("Checking " + ((Collection<?>) paused).size() + " locks on resume_date");
            for (RedisLock redisLock : paused) {
                if(redisLock.getResume_date() == null)
                    continue;

                if(redisLock.getResume_date().isAfter(LocalDateTime.now())) {
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
