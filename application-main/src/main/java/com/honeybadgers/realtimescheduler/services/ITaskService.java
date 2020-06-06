package com.honeybadgers.realtimescheduler.services;

import com.honeybadgers.realtimescheduler.model.RedisTask;
import com.honeybadgers.models.Task;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface ITaskService {

    List<Task> getAllTasks();

    Optional<Task> getTaskById(String id);

    void uploadTask(Task task);

    void deleteTask(String id);

    long calculatePriority(Task task);
}
