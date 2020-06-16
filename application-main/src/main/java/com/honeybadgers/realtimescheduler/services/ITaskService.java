package com.honeybadgers.realtimescheduler.services;

import com.honeybadgers.models.model.Task;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface ITaskService {

    List<Task> getAllTasks();

    Optional<Task> getTaskById(String id);

    List<String> getRecursiveGroupsOfTask(String taskId);

    void uploadTask(Task task);

    void deleteTask(String id);

    long calculatePriority(Task task);
}
