package com.honeybadgers.realtimescheduler.services;

import com.honeybadgers.realtimescheduler.model.Task;
import com.honeybadgers.realtimescheduler.repository.TaskPostgresRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class TaskService implements ITaskService {

    @Autowired
    TaskPostgresRepository taskPostgresRepository;

    @Override
    public List<Task> getAllTasks() {
        return taskPostgresRepository.findAll();
    }

    @Override
    public void uploadTask(Task task) {
        taskPostgresRepository.save(task);
    }

    @Override
    public void deleteTask(String id) {
        this.taskPostgresRepository.deleteById(id);
    }
}
