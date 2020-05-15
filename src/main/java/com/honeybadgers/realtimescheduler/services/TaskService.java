package com.honeybadgers.realtimescheduler.services;

import com.honeybadgers.realtimescheduler.model.Task;
import com.honeybadgers.realtimescheduler.repository.TaskPostgresRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class TaskService {

    @Autowired
    TaskPostgresRepository taskPostgresRepository;

    public List<Task> getAllTasks() {
        return this.taskPostgresRepository.findAll();
    }
}
