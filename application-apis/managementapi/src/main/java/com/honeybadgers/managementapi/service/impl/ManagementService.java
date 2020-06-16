package com.honeybadgers.managementapi.service.impl;

import com.honeybadgers.managementapi.exception.LockException;
import com.honeybadgers.managementapi.repository.StateRepository;
import com.honeybadgers.managementapi.service.IManagementService;
import com.honeybadgers.models.model.RedisLock;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;

import static com.honeybadgers.models.model.Constants.*;

@Service
public class ManagementService implements IManagementService {

    static final Logger logger = LogManager.getLogger(ManagementService.class);


    @Autowired
    StateRepository stateRepository;

    @Override
    @Transactional
    public void pauseScheduler(OffsetDateTime resumeDate) throws LockException{
        RedisLock lockObj = stateRepository.findById(LOCK_SCHEDULER_ALIAS).orElse(null);

        if(lockObj != null)
            throw new LockException("Already locked!");

        RedisLock toSave = new RedisLock();
        toSave.setId(LOCK_SCHEDULER_ALIAS);
        if(resumeDate != null)
            toSave.setResume_date(LocalDateTime.ofEpochSecond(resumeDate.toEpochSecond(), 0, resumeDate.getOffset()));
        stateRepository.save(toSave);
    }

    @Override
    public void resumeScheduler() {
        stateRepository.deleteById(LOCK_SCHEDULER_ALIAS);
    }

    @Override
    @Transactional
    public void pauseTask(UUID taskId, OffsetDateTime resumeDate) throws LockException{
        String id = LOCK_TASK_PREFIX + taskId.toString();
        RedisLock lockId = stateRepository.findById(id).orElse(null);

        if(lockId != null)
            throw new LockException("Already locked!");

        RedisLock toSave = new RedisLock();
        toSave.setId(id);
        if(resumeDate != null)
            toSave.setResume_date(LocalDateTime.ofEpochSecond(resumeDate.toEpochSecond(), 0, resumeDate.getOffset()));
        stateRepository.save(toSave);
    }

    @Override
    public void resumeTask(UUID taskId) {
        String id = LOCK_TASK_PREFIX + taskId.toString();
        stateRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void pauseGroup(String groupId, OffsetDateTime resumeDate) throws LockException{
        String id = LOCK_GROUP_PREFIX + groupId;
        RedisLock lockId = stateRepository.findById(id).orElse(null);

        if(lockId != null)
            throw new LockException("Already locked!");

        RedisLock toSave = new RedisLock();
        toSave.setId(id);
        if(resumeDate != null)
            toSave.setResume_date(LocalDateTime.ofEpochSecond(resumeDate.toEpochSecond(), 0, resumeDate.getOffset()));
        logger.info("############ SAVING: " + toSave.toString());
        stateRepository.save(toSave);
    }

    @Override
    public void resumeGroup(String grouId) {
        String id = LOCK_GROUP_PREFIX + grouId;
        stateRepository.deleteById(id);
    }


}
