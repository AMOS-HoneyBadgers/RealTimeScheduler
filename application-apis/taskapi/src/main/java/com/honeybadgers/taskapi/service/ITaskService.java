package com.honeybadgers.taskapi.service;

import com.honeybadgers.communication.model.TaskQueueModel;
import com.honeybadgers.models.Task;
import com.honeybadgers.models.UnknownEnumException;
import com.honeybadgers.taskapi.exceptions.CreationException;
import com.honeybadgers.taskapi.exceptions.JpaException;
import com.honeybadgers.taskapi.models.TaskModel;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public interface ITaskService {

    Task createTask(TaskModel restModel) throws JpaException, UnknownEnumException, CreationException;
    List<TaskModel> getAllTasks();
    void sendTaskToTaskEventQueue(String taskId);
    //TODO specify which type should be sent to the dispatcher
    void sendTaskToPriorityQueue(TaskModel task);
}
