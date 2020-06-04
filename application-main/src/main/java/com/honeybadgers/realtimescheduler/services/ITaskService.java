package com.honeybadgers.realtimescheduler.services;

import com.honeybadgers.realtimescheduler.model.RedisTask;
import com.honeybadgers.models.Task;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ITaskService {

    List<Task> getAllTasks();

    void uploadTask(Task task);

    void deleteTask(String id);

    long calculatePriority(Task task);

    void scheduleTask(Task task);

    List<RedisTask> getAllRedisTasks();
}
