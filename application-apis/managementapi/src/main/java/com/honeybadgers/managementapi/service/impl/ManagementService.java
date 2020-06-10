package com.honeybadgers.managementapi.service.impl;

import com.honeybadgers.managementapi.exception.LockException;
import com.honeybadgers.managementapi.repository.StateRepository;
import com.honeybadgers.managementapi.service.IManagementService;
import com.honeybadgers.models.RedisLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class ManagementService implements IManagementService {


    // These prefixes are for the case, that a group exists with id='SCHEDULER_LOCK_ALIAS'
    // HAVE TO BE THE SAME AS IN SchedulerService IN realtimescheduler-main!!!!!!!!!!!!!
    public static final String SCHEDULER_ALIAS = "SCHEDULER_LOCK_ALIAS";
    public static final String TASK_PREFIX = "TASK:";
    public static final String GROUP_PREFIX = "GROUP:";


    @Autowired
    StateRepository stateRepository;

    @Override
    @Transactional
    public void pauseScheduler(OffsetDateTime resumeDate) throws LockException{
        RedisLock lockObj = stateRepository.findById(SCHEDULER_ALIAS).orElse(null);

        if(lockObj != null)
            throw new LockException("Already locked!");

        RedisLock toSave = new RedisLock();
        toSave.setId(SCHEDULER_ALIAS);
        if(resumeDate != null)
            toSave.setResume_date(LocalDateTime.ofEpochSecond(resumeDate.toEpochSecond(), 0, resumeDate.getOffset()));
        stateRepository.save(toSave);
    }

    @Override
    public void resumeScheduler() {
        stateRepository.deleteById(SCHEDULER_ALIAS);
    }

    @Override
    @Transactional
    public void pauseTask(UUID taskId, OffsetDateTime resumeDate) throws LockException{
        String id = TASK_PREFIX + taskId.toString();
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
        String id = TASK_PREFIX + taskId.toString();
        stateRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void pauseGroup(String groupId, OffsetDateTime resumeDate) throws LockException{
        String id = GROUP_PREFIX + groupId;
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
    public void resumeGroup(String grouId) {
        String id = GROUP_PREFIX + grouId;
        stateRepository.deleteById(id);
    }


}
