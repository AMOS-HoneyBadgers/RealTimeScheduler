package com.honeybadgers.realtimescheduler.services;

import com.honeybadgers.models.model.History;
import com.honeybadgers.models.model.Task;
import com.honeybadgers.models.model.TaskStatusEnum;
import jdk.jshell.spi.ExecutionControl;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
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

    void updateTaskhistory(Task task, TaskStatusEnum status) throws RuntimeException;
}
