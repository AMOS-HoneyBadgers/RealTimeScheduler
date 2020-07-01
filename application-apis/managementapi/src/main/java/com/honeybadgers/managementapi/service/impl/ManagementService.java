package com.honeybadgers.managementapi.service.impl;

import com.honeybadgers.communication.ICommunication;
import com.honeybadgers.managementapi.exception.LockException;
import com.honeybadgers.managementapi.service.IManagementService;
import com.honeybadgers.models.model.Paused;
import com.honeybadgers.postgre.repository.PausedRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import static com.honeybadgers.models.model.Constants.*;

@Service
public class ManagementService implements IManagementService {

    @Value("${scheduler.trigger}")
    String scheduler_trigger;

    @Autowired
    PausedRepository pausedRepository;

    @Autowired
    ICommunication sender;

    static final Logger logger = LogManager.getLogger(ManagementService.class);

    @Override
    public void pauseScheduler(OffsetDateTime resumeDate) throws LockException{
        Paused lockObj = pausedRepository.findById(PAUSED_SCHEDULER_ALIAS).orElse(null);

        if(lockObj != null)
            throw new LockException("Already locked!");

        Paused toSave = new Paused();
        toSave.setId(PAUSED_SCHEDULER_ALIAS);
        if(resumeDate != null)
            toSave.setResumeDate(Timestamp.valueOf(LocalDateTime.ofEpochSecond(resumeDate.toEpochSecond(), 0, ZoneOffset.UTC)));
        pausedRepository.save(toSave);
    }

    @Override
    public void resumeScheduler() {
        pausedRepository.deleteById(PAUSED_SCHEDULER_ALIAS);
    }

    @Override
    public void pauseTask(UUID taskId, OffsetDateTime resumeDate) throws LockException{
        String id = PAUSED_TASK_PREFIX + taskId.toString();
        Paused lockId = pausedRepository.findById(id).orElse(null);

        if(lockId != null)
            throw new LockException("Already locked!");

        Paused toSave = new Paused();
        toSave.setId(id);
        if(resumeDate != null)
            toSave.setResumeDate(Timestamp.valueOf(LocalDateTime.ofEpochSecond(resumeDate.toEpochSecond(), 0, ZoneOffset.UTC)));
        pausedRepository.save(toSave);
    }

    @Override
    public void resumeTask(UUID taskId) {
        String id = PAUSED_TASK_PREFIX + taskId.toString();
        pausedRepository.deleteById(id);
    }

    @Override
    public void pauseGroup(String groupId, OffsetDateTime resumeDate) throws LockException{
        String id = PAUSED_GROUP_PREFIX + groupId;
        Paused lockId = pausedRepository.findById(id).orElse(null);


        if(lockId != null)
            throw new LockException("Already locked!");

        Paused toSave = new Paused();
        toSave.setId(id);
        if(resumeDate != null)
            toSave.setResumeDate(Timestamp.valueOf(LocalDateTime.ofEpochSecond(resumeDate.toEpochSecond(), 0, ZoneOffset.UTC)));
        pausedRepository.save(toSave);
    }

    @Override
    public void resumeGroup(String grouId) {
        String id = PAUSED_GROUP_PREFIX + grouId;
        pausedRepository.deleteById(id);
    }
}
