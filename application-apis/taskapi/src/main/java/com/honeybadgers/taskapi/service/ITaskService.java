package com.honeybadgers.taskapi.service;

import com.honeybadgers.models.Task;
import com.honeybadgers.models.UnknownEnumException;
import com.honeybadgers.taskapi.exceptions.CreationException;
import com.honeybadgers.taskapi.exceptions.JpaException;
import com.honeybadgers.taskapi.models.TaskModel;
import org.springframework.stereotype.Service;

@Service
public interface ITaskService {

    Task createTask(TaskModel restModel) throws JpaException, UnknownEnumException, CreationException;
        void sendTaskToTaskEventQueue(String taskId);
}
