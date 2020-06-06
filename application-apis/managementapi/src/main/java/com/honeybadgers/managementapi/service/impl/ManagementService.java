package com.honeybadgers.managementapi.service.impl;

import com.honeybadgers.managementapi.repository.StateRepository;
import com.honeybadgers.managementapi.service.IManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
public class ManagementService implements IManagementService {

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
        /*RedisTask taskPaused = stateRepository.findById(task).orElse(null);

        if(taskPaused == null){

        }*/

    }

    @Override
    public void resumeTask(Long task) {

    }


}
