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
import org.hibernate.exception.LockAcquisitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.dao.DataIntegrityViolationException;
import com.honeybadgers.taskapi.exceptions.JpaException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.IllegalTransactionStateException;

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
    @Qualifier("taskConvertUtils")
    @Autowired
    ITaskConvertUtils converter;

    @Value("${scheduler.trigger}")
    String scheduler_trigger;

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
    public Task createTask(TaskModel restModel) throws JpaException, UnknownEnumException, CreationException, InterruptedException {
        int iteration =1;
        while (true){
            try{
                Task checkTask = taskRepository.findById(restModel.getId()).orElse(null);
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
            } catch (CannotAcquireLockException | LockAcquisitionException exception){
                double timeToSleep= Math.random()*1000*iteration;
                logger.warn("Task " + restModel.getId() + " couldn't acquire locks for setting its status to finished. Try again after "+timeToSleep+" milliseconds" );
                Thread.sleep(Math.round(timeToSleep));
                iteration++;
            }
        }
    }

    @Override
    public Task updateTask(String taskId, TaskModel restModel) throws UnknownEnumException, JpaException, CreationException, InterruptedException {
        int iteration =1;
        while (true){
            try{
                Task checkTask = taskRepository.findById(taskId).orElse(null);
                if(checkTask == null)
                    throw new NoSuchElementException("No existing Task with id: " + taskId);

                if(checkTask.isForce() == true && restModel.getForce() == false)
                    throw new IllegalStateException("Task " + taskId +  " already bypassed scheduling process. Cannot schedule now");

                restModel.setId(taskId);
                Task updatedTask = converter.taskRestToJpa(restModel);

                if(updatedTask.getStatus() != TaskStatusEnum.Scheduled && updatedTask.getStatus() != TaskStatusEnum.Waiting)
                    throw new IllegalStateException("Task: " + updatedTask.getId() + " already dispatched");

                updatedTask.setStatus(TaskStatusEnum.Waiting);

                try {
                    taskRepository.save(updatedTask);
                    sender.sendTaskToTasksQueue(scheduler_trigger);
                } catch (DataIntegrityViolationException e) {
                    logger.error("DataIntegrityViolation on save new task!");
                    logger.error(e.getStackTrace());
                    throw new JpaException("DataIntegrityViolation on save new task!");
                }

                return updatedTask;
            }
            catch (LockAcquisitionException | CannotAcquireLockException exception){
                double timeToSleep= Math.random()*1000*iteration;
                logger.error("Task " + restModel.getId() + " couldn't acquire locks for setting its status to finished. Try again after "+timeToSleep+" milliseconds" );
                Thread.sleep(Math.round(timeToSleep));
                iteration++;
            }
        }
    }

    @Override
    public TaskModel getTaskById(String taskid) {
        Task task = taskRepository.findById(taskid).orElse(null);
        if(task == null)
            throw new NoSuchElementException("No existing Task with ID: " + taskid);

        return converter.taskJpaToRest(task);
    }

    @Override
    public TaskModel deleteTask(String taskid) {
        // TODO custom query
        Task task = taskRepository.findById(taskid).orElse(null);
        if(task == null)
            throw new NoSuchElementException("No existing Task with ID: " + taskid);

        taskRepository.deleteById(taskid);
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
        taskQueueModel.setId(task.getId());
        if (task.getMeta() != null)
            taskQueueModel.setMetaData(task.getMeta().stream().collect(Collectors.toMap(TaskModelMeta::getKey, TaskModelMeta::getValue)));
        taskQueueModel.setDispatched(Timestamp.from(Instant.now()));
        sender.sendTaskToPriorityQueue(taskQueueModel);
    }
}
