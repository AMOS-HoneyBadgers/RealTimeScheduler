package com.honeybadgers.taskapi.service;

import com.honeybadgers.models.model.Task;
import com.honeybadgers.models.model.UnknownEnumException;
import com.honeybadgers.taskapi.exceptions.CreationException;
import com.honeybadgers.taskapi.exceptions.JpaException;
import com.honeybadgers.taskapi.models.TaskModel;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;


@Service
public interface ITaskService {

    Task createTask(TaskModel restModel) throws JpaException, UnknownEnumException, CreationException;
    Task updateTask(UUID taskId, TaskModel restModel) throws UnknownEnumException, JpaException, CreationException;
    List<TaskModel> getAllTasks();
    TaskModel deleteTask(UUID taskid) throws NoSuchElementException;
    TaskModel getTaskById(UUID taskid) throws NoSuchElementException;
    void sendTaskToTaskEventQueue(String taskId);
    //TODO specify which type should be sent to the dispatcher
    void sendTaskToPriorityQueue(TaskModel task);
}
