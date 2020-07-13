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
    /**
     * Create a Task and saves it in the Database.
     * @param restModel TaskModel received via Rest.
     * @return crated Task as JPA Model .
     * @throws JpaException Task already exists or Database action error.
     * @throws UnknownEnumException Mode or Type does not exist.
     * @throws CreationException Groups can either have other Groups or Tasks violation.
     */
    Task createTask(TaskModel restModel) throws JpaException, UnknownEnumException, InterruptedException, CreationException;

    /**
     * Update a Task in in the Database.
     * @param taskId id of Task to change.
     * @param restModel TaskModel with updated attributes.
     * @return updated Task as JPA Model.
     * @throws UnknownEnumException Mode or Type does not exist.
     * @throws JpaException Database action error.
     * @throws CreationException Groups can either have other Groups or Tasks violation.
     * @throws IllegalStateException illegal update on dispatched Task.
     */
    Task updateTask(String taskId, TaskModel restModel) throws UnknownEnumException, JpaException, CreationException, InterruptedException, IllegalStateException;

    /**
     * Get all Tasks from Database.
     * @return List of all Tasks.
     */
    List<TaskModel> getAllTasks();

    /**
     * Delete Task from Database.
     * @param taskid id of specified Task.
     * @return deleted Task.
     * @throws NoSuchElementException specified Task does not exist.
     */
    @Transactional(isolation = Isolation.SERIALIZABLE)
    TaskModel deleteTask(String taskid) throws NoSuchElementException, InterruptedException, JpaException;

    /**
     * Get single Task.
     * @param taskid id of specified Task.
     * @return Task as Rest Model.
     * @throws NoSuchElementException specified Task does not exist.
     */
    TaskModel getTaskById(String taskid) throws NoSuchElementException, InterruptedException, JpaException;

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
