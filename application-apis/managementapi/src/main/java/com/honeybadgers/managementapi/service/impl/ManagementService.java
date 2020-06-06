package com.honeybadgers.managementapi.service.impl;

import com.honeybadgers.managementapi.exception.LockException;
import com.honeybadgers.managementapi.repository.StateRepository;
import com.honeybadgers.managementapi.service.IManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        String lockId = stateRepository.findById(SCHEDULER_ALIAS).orElse(null);

        if(lockId != null)
            throw new LockException("Already locked!");

        stateRepository.save(SCHEDULER_ALIAS);
    }

    @Override
    public void resumeScheduler() {
        stateRepository.deleteById(SCHEDULER_ALIAS);
    }

    @Override
    @Transactional
    public void pauseTask(UUID taskId, OffsetDateTime resumeDate) throws LockException{
        String id = TASK_PREFIX + taskId.toString();
        String lockId = stateRepository.findById(id).orElse(null);

        if(lockId != null)
            throw new LockException("Already locked!");

        stateRepository.save(id);
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
        String lockId = stateRepository.findById(id).orElse(null);

        if(lockId != null)
            throw new LockException("Already locked!");

        stateRepository.save(id);
    }

    @Override
    public void resumeGroup(String grouId) {
        String id = GROUP_PREFIX + grouId;
        stateRepository.deleteById(id);
    }


}
