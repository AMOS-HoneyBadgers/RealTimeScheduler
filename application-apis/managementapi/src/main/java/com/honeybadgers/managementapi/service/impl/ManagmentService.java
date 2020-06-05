package com.honeybadgers.managementapi.service.impl;

import com.honeybadgers.managementapi.repository.StateRepository;
import com.honeybadgers.managementapi.service.IManagmentService;
import com.honeybadgers.models.RedisTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
public class ManagmentService implements IManagmentService {

    @Autowired
    StateRepository stateRepository;

    @Override
    public void pauseScheduler() {

    }

    @Override
    public void resumeScheduler() {

    }

    @Override
    public void pauseTask(Long task, OffsetDateTime resumeDate) {
        RedisTask taskPaused = stateRepository.findById(task.toString()).orElse(null);

        if(taskPaused == null){

        }

    }

    @Override
    public void resumeTask(Long task) {

    }


}
