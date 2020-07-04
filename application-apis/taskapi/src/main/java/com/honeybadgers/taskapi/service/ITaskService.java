package com.honeybadgers.taskapi.service;

import com.honeybadgers.models.model.Task;
import com.honeybadgers.models.model.UnknownEnumException;
import com.honeybadgers.taskapi.exceptions.CreationException;
import com.honeybadgers.taskapi.exceptions.JpaException;
import com.honeybadgers.taskapi.models.TaskModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;


@Service
public interface ITaskService {

    @Transactional(isolation = Isolation.SERIALIZABLE)
    Task createTask(TaskModel restModel) throws JpaException, UnknownEnumException, CreationException, InterruptedException;

    @Transactional(isolation = Isolation.SERIALIZABLE)
    Task updateTask(String taskId, TaskModel restModel) throws UnknownEnumException, JpaException, CreationException, InterruptedException;

    List<TaskModel> getAllTasks();

    @Transactional(isolation = Isolation.SERIALIZABLE)
    TaskModel deleteTask(String taskid) throws NoSuchElementException;

    TaskModel getTaskById(String taskid) throws NoSuchElementException;

    void sendTaskToTaskEventQueue(String taskId);

    //TODO specify which type should be sent to the dispatcher
    void sendTaskToPriorityQueue(TaskModel task);
}
