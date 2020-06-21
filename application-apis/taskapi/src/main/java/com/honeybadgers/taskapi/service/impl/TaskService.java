package com.honeybadgers.taskapi.service.impl;

import com.honeybadgers.communication.ICommunication;
import com.honeybadgers.communication.model.TaskQueueModel;
import com.honeybadgers.models.model.*;
import com.honeybadgers.postgre.repository.GroupRepository;
import com.honeybadgers.postgre.repository.TaskRepository;
import com.honeybadgers.taskapi.exceptions.CreationException;
import com.honeybadgers.taskapi.models.TaskModel;
import com.honeybadgers.taskapi.models.TaskModelMeta;
import com.honeybadgers.taskapi.service.ITaskConvertUtils;
import com.honeybadgers.taskapi.service.ITaskService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import com.honeybadgers.taskapi.exceptions.JpaException;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TaskService implements ITaskService {

    @Autowired
    GroupRepository groupRepository;
    @Autowired
    TaskRepository taskRepository;
    @Autowired
    ICommunication sender;
    @Autowired
    ITaskConvertUtils converter;

    static final Logger logger = LogManager.getLogger(TaskService.class);

    @Override
    public List<TaskModel> getAllTasks() {
        List<TaskModel> taskModelList;
        List<Task> taskList = taskRepository.findAll();

        taskModelList = taskList.stream().map(t -> {
            TaskModel restModel = converter.taskJpaToRest(t);
            return restModel;
        }).collect(Collectors.toList());

        return taskModelList;
    }

    @Override
    public Task createTask(TaskModel restModel) throws JpaException, UnknownEnumException, CreationException {
        Task checkTask = taskRepository.findById(restModel.getId().toString()).orElse(null);
        if( checkTask != null ){
            throw new JpaException("Primary or unique constraint failed!");
        }

        Task newTask = converter.taskRestToJpa(restModel);

        try {
            taskRepository.save(newTask);
        } catch (DataIntegrityViolationException e) {
            logger.error("DataIntegrityViolation on save new task!");
            logger.error(e.getStackTrace());
            throw new JpaException("DataIntegrityViolation on save new task!");
        }
        return newTask;
    }

    @Override
    public Task updateTask(UUID taskId, TaskModel restModel) throws UnknownEnumException, JpaException, CreationException {
        Task checkTask = taskRepository.findById(taskId.toString()).orElse(null);
        if(checkTask == null)
            throw new NoSuchElementException("No existing Task with id: " + taskId);

        if(checkTask.isForce() == true && restModel.getForce() == false)
            throw new IllegalStateException("Task " + taskId +  " already bypassed scheduling process. Cannot schedule now");

        restModel.setId(taskId);
        Task updatedTask = converter.taskRestToJpa(restModel);

        try {
            taskRepository.save(updatedTask);
        } catch (DataIntegrityViolationException e) {
            logger.error("DataIntegrityViolation on save new task!");
            logger.error(e.getStackTrace());
            throw new JpaException("DataIntegrityViolation on save new task!");
        }

        return updatedTask;
    }

    @Override
    public TaskModel getTaskById(UUID taskid) {
        Task task = taskRepository.findById(taskid.toString()).orElse(null);
        if(task == null)
            throw new NoSuchElementException("No existing Task with ID: " + taskid);

        return converter.taskJpaToRest(task);
    }

    @Override
    public TaskModel deleteTask(UUID taskid) {
        Task task = taskRepository.findById(taskid.toString()).orElse(null);
        if(task == null)
            throw new NoSuchElementException("No existing Task with ID: " + taskid);

        taskRepository.deleteById(taskid.toString());
        return converter.taskJpaToRest(task);
    }


    @Override
    public void sendTaskToTaskEventQueue(String taskId) {
        sender.sendTaskToTasksQueue(taskId);
    }

    @Override
    public void sendTaskToPriorityQueue(TaskModel task) {
        TaskQueueModel taskQueueModel = new TaskQueueModel();
        taskQueueModel.setGroupId(task.getGroupId());
        taskQueueModel.setId(task.getId().toString());
        if (task.getMeta() != null)
            taskQueueModel.setMetaData(task.getMeta().stream().collect(Collectors.toMap(TaskModelMeta::getKey, TaskModelMeta::getValue)));
        taskQueueModel.setDispatched(Timestamp.from(Instant.now()));
        sender.sendTaskToPriorityQueue(taskQueueModel);
    }
}
