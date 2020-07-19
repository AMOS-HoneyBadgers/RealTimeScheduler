package com.honeybadgers.taskapi.service;

import com.honeybadgers.models.model.jpa.Task;
import com.honeybadgers.models.exceptions.UnknownEnumException;
import com.honeybadgers.models.exceptions.CreationException;
import com.honeybadgers.models.exceptions.JpaException;
import com.honeybadgers.models.exceptions.TransactionRetriesExceeded;
import com.honeybadgers.taskapi.models.TaskModel;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;


@Service
public interface ITaskService {
    /**
     * Create a Task and saves it in the Database.
     * @param restModel TaskModel received via Rest.
     * @return crated Task as JPA Model .
     * @throws JpaException Task already exists or Database action error.
     * @throws UnknownEnumException Mode or Type does not exist.
     * @throws CreationException Groups can either have other Groups or Tasks violation.
     * @throws InterruptedException Thread.sleep error for retry
     * @throws TransactionRetriesExceeded if transaction has failed n times (configurable in application.properties)
     */
    Task createTask(TaskModel restModel) throws JpaException, UnknownEnumException, InterruptedException, CreationException, TransactionRetriesExceeded;

    /**
     * Update a Task in in the Database.
     * @param taskId id of Task to change.
     * @param restModel TaskModel with updated attributes.
     * @return updated Task as JPA Model.
     * @throws UnknownEnumException Mode or Type does not exist.
     * @throws JpaException Database action error.
     * @throws CreationException Groups can either have other Groups or Tasks violation.
     * @throws IllegalStateException illegal update on dispatched Task.
     * @throws InterruptedException Thread.sleep error for retry
     * @throws TransactionRetriesExceeded if transaction has failed n times (configurable in application.properties)
     */
    Task updateTask(String taskId, TaskModel restModel) throws UnknownEnumException, JpaException, CreationException, InterruptedException, IllegalStateException, TransactionRetriesExceeded;

    /**
     * Get all Tasks from Database.
     * @return List of all Tasks.
     */
    List<TaskModel> getAllTasks() throws InterruptedException, TransactionRetriesExceeded;

    /**
     * Delete Task from Database.
     * @param taskid id of specified Task.
     * @return deleted Task.
     * @throws NoSuchElementException specified Task does not exist.
     * @throws InterruptedException Thread.sleep error for retry
     * @throws TransactionRetriesExceeded if transaction has failed n times (configurable in application.properties)
     */
    TaskModel deleteTask(String taskid) throws NoSuchElementException, InterruptedException, TransactionRetriesExceeded;

    /**
     * Get single Task.
     * @param taskid id of specified Task.
     * @return Task as Rest Model.
     * @throws NoSuchElementException specified Task does not exist.
     * @throws InterruptedException Thread.sleep error for retry
     * @throws TransactionRetriesExceeded if transaction has failed n times (configurable in application.properties)
     */
    TaskModel getTaskById(String taskid) throws NoSuchElementException, InterruptedException, TransactionRetriesExceeded;

    /**
     * Send Task to Scheduler Task Queue.
     * @param taskId id of specified Task.
     */
    void sendTaskToTaskEventQueue(String taskId);

    //TODO specify which type should be sent to the dispatcher
    /**
     * Dispatch Task immediately skipping Scheduling.
     * @param task Task received via Rest.
     */
    void sendTaskToPriorityQueue(TaskModel task);
}
