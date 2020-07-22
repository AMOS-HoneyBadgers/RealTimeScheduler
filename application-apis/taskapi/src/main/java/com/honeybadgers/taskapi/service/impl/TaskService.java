package com.honeybadgers.taskapi.service.impl;

import com.honeybadgers.communication.ICommunication;
import com.honeybadgers.communication.model.TaskQueueModel;
import com.honeybadgers.models.exceptions.CreationException;
import com.honeybadgers.models.exceptions.JpaException;
import com.honeybadgers.models.exceptions.TransactionRetriesExceeded;
import com.honeybadgers.models.exceptions.UnknownEnumException;
import com.honeybadgers.models.model.jpa.Task;
import com.honeybadgers.models.model.jpa.TaskStatusEnum;
import com.honeybadgers.postgre.repository.GroupRepository;
import com.honeybadgers.postgre.repository.TaskRepository;
import com.honeybadgers.taskapi.models.TaskModel;
import com.honeybadgers.taskapi.service.ITaskConvertUtils;
import com.honeybadgers.taskapi.service.ITaskService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.TransactionException;
import org.hibernate.exception.LockAcquisitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
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
    @Qualifier("taskConvertUtils")
    ITaskConvertUtils converter;

    @Autowired
    TaskService _self;

    @Value("${scheduler.trigger}")
    String scheduler_trigger;

    @Value("${com.honeybadgers.transaction.max-retry-sleep:500}")
    int maxTransactionRetrySleep;

    @Value("${com.honeybadgers.transaction.max-retry-count:5}")
    int maxTransactionRetryCount;

    static final Logger logger = LogManager.getLogger(TaskService.class);

    @Override
    public List<TaskModel> getAllTasks() throws InterruptedException, TransactionRetriesExceeded {
        int iteration = 1;
        while (iteration <= maxTransactionRetryCount) {
            try {
                List<TaskModel> taskModelList;
                List<Task> taskList = taskRepository.findAll();

                taskModelList = taskList.stream().map(t -> {
                    TaskModel restModel = converter.taskJpaToRest(t);
                    return restModel;
                }).collect(Collectors.toList());

                return taskModelList;
            } catch (JpaSystemException | TransactionException | CannotAcquireLockException | LockAcquisitionException exception) {
                // TransactionException is nested ex of JpaSystemException and LockAcquisitionException is nested of CannotAcquireLockException
                double timeToSleep = Math.random() * maxTransactionRetrySleep * iteration;
                logger.warn("Transaction exception while getting all tasks. Try again after " + timeToSleep + " milliseconds");
                Thread.sleep(Math.round(timeToSleep));
                iteration++;
            }
        }
        // throw exception due to surpassing max retries
        throw new TransactionRetriesExceeded("Failed transaction " + maxTransactionRetryCount + " times!");
    }

    @Override
    public Task createTask(TaskModel restModel) throws JpaException, UnknownEnumException, CreationException, InterruptedException, TransactionRetriesExceeded {
        int iteration = 1;
        while (iteration <= maxTransactionRetryCount) {
            try {
                return _self.createTaskInternal(restModel);
            } catch (JpaSystemException | TransactionException | CannotAcquireLockException | LockAcquisitionException exception) {
                // TransactionException is nested ex of JpaSystemException and LockAcquisitionException is nested of CannotAcquireLockException
                double timeToSleep = Math.random() * maxTransactionRetrySleep * iteration;
                logger.warn("Task " + restModel.getId() + " transaction exception while creating task. Try again after " + timeToSleep + " milliseconds");
                Thread.sleep(Math.round(timeToSleep));
                iteration++;
            }
        }
        // throw exception due to surpassing max retries
        throw new TransactionRetriesExceeded("Failed transaction " + maxTransactionRetryCount + " times!");
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Task createTaskInternal(TaskModel restModel) throws JpaException, UnknownEnumException, CreationException {
        Task checkTask = taskRepository.findById(restModel.getId()).orElse(null);
        if (checkTask != null) {
            throw new JpaException("Primary or unique constraint failed!");
        }

        Task newTask = converter.taskRestToJpa(restModel);

        try {
            taskRepository.save(newTask);
        } catch (DataIntegrityViolationException e) {
            logger.error("DataIntegrityViolation on save new task!");
            logger.error(Arrays.deepToString(e.getStackTrace()));
            throw new JpaException("DataIntegrityViolation on save new task!");
        }
        return newTask;
    }

    @Override
    public Task updateTask(String taskId, TaskModel restModel) throws UnknownEnumException, JpaException, CreationException, InterruptedException, IllegalStateException, TransactionRetriesExceeded {
        int iteration = 1;
        while (iteration <= maxTransactionRetryCount) {
            try {
                return _self.updateTaskInternal(taskId, restModel);
            } catch (JpaSystemException | TransactionException | CannotAcquireLockException | LockAcquisitionException exception) {
                // TransactionException is nested ex of JpaSystemException and LockAcquisitionException is nested of CannotAcquireLockException
                double timeToSleep = Math.random() * maxTransactionRetrySleep * iteration;
                logger.error("Task " + restModel.getId() + " transaction exception while updating task. Try again after " + timeToSleep + " milliseconds");
                Thread.sleep(Math.round(timeToSleep));
                iteration++;
            }
        }
        // throw exception due to surpassing max retries
        throw new TransactionRetriesExceeded("Failed transaction " + maxTransactionRetryCount + " times!");
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Task updateTaskInternal(String taskId, TaskModel restModel) throws UnknownEnumException, JpaException, CreationException {
        Task checkTask = taskRepository.findById(taskId).orElse(null);
        if (checkTask == null)
            throw new NoSuchElementException("No existing Task with id: " + taskId);

        if (checkTask.isForce() == true && restModel.getForce() == false)
            throw new IllegalStateException("Task " + taskId + " already bypassed scheduling process. Cannot schedule now");

        restModel.setId(taskId);
        Task updatedTask = converter.taskRestToJpa(restModel);

        if (updatedTask.getStatus() != TaskStatusEnum.Scheduled && updatedTask.getStatus() != TaskStatusEnum.Waiting)
            throw new IllegalStateException("Task: " + updatedTask.getId() + " already dispatched");

        updatedTask.setStatus(TaskStatusEnum.Waiting);

        try {
            taskRepository.save(updatedTask);
            sender.sendTaskToTasksQueue(scheduler_trigger);
        } catch (DataIntegrityViolationException e) {
            logger.error("DataIntegrityViolation on save new task!");
            logger.error(Arrays.deepToString(e.getStackTrace()));
            throw new JpaException("DataIntegrityViolation on save new task!");
        }

        return updatedTask;
    }

    @Override
    public TaskModel getTaskById(String taskid) throws InterruptedException, TransactionRetriesExceeded {
        int iteration = 1;
        while (iteration <= maxTransactionRetryCount) {
            try {
                Task task = taskRepository.findById(taskid).orElse(null);
                if (task == null)
                    throw new NoSuchElementException("No existing Task with ID: " + taskid);

                return converter.taskJpaToRest(task);
            } catch (JpaSystemException | TransactionException | CannotAcquireLockException | LockAcquisitionException exception) {
                // TransactionException is nested ex of JpaSystemException and LockAcquisitionException is nested of CannotAcquireLockException
                double timeToSleep = Math.random() * maxTransactionRetrySleep * iteration;
                logger.error("Task " + taskid + " transaction exception while getting task. Try again after " + timeToSleep + " milliseconds");
                Thread.sleep(Math.round(timeToSleep));
                iteration++;
            }
        }
        // throw exception due to surpassing max retries
        throw new TransactionRetriesExceeded("Failed transaction " + maxTransactionRetryCount + " times!");
    }

    @Override
    public TaskModel deleteTask(String taskid) throws InterruptedException, TransactionRetriesExceeded {
        int iteration = 1;
        while (iteration <= maxTransactionRetryCount) {
            try {
                Task task = taskRepository.deleteByIdCustomQuery(taskid).orElse(null);
                if (task == null)
                    throw new NoSuchElementException("No existing Task with ID: " + taskid);
                return converter.taskJpaToRest(task);
            } catch (JpaSystemException | TransactionException | CannotAcquireLockException | LockAcquisitionException exception) {
                // TransactionException is nested ex of JpaSystemException and LockAcquisitionException is nested of CannotAcquireLockException
                double timeToSleep = Math.random() * maxTransactionRetrySleep * iteration;
                logger.error("Task " + taskid + " transaction exception while deleting task. Try again after " + timeToSleep + " milliseconds");
                Thread.sleep(Math.round(timeToSleep));
                iteration++;
            }
        }
        // throw exception due to surpassing max retries
        throw new TransactionRetriesExceeded("Failed transaction " + maxTransactionRetryCount + " times!");
    }


    @Override
    public void sendTaskToTaskEventQueue(String taskId) {

        sender.sendTaskToTasksQueue(taskId);
    }

    @Override
    public void sendTaskToPriorityQueue(TaskModel task) {
        TaskQueueModel taskQueueModel = converter.taskRestToQueue(task);

        sender.sendTaskToPriorityQueue(taskQueueModel);
    }
}
