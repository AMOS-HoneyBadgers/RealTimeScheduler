package com.honeybadgers.realtimescheduler.services;


import com.honeybadgers.models.model.jpa.Task;
import com.honeybadgers.models.model.jpa.TaskStatusEnum;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public interface ITaskService {

    /**
     * Warning: not transaction save (no try catch etc)
     * Returns a single tasks of the taskRepository
     *
     * @param id id of the task
     * @return taskModel
     */
    Optional<Task> getTaskById(String id);

    /**
     * Gets the parent group of task with given taskId and ALL of its parents (recursively using tree of postgres)
     * For more information concerning the query see the javadoc of GroupAncestorRepository.getAllAncestorIdsFromGroup()
     *
     * @param taskId taskId of which task all groups are wanted
     * @return List of the ids of all group and their ancestors
     */
    @Transactional(isolation = Isolation.SERIALIZABLE)
    List<String> getRecursiveGroupsOfTask(String taskId);

    /**
     * Warning: not transaction save (no try catch etc)
     * Set task status to finished
     *
     * @param task taskModel
     */
    void finishTask(Task task);

    /**
     * Main priority calculation of the task. Algorithm is processed (Specified in docs, can be modified with variables)
     *
     * @param task id of the task
     * @return calculated priority
     */
    long calculatePriority(Task task);

    /**
     * Updates the status of the task to the handed status
     *
     * @param task   id of the task
     * @param status status to which should be updated
     * @throws RuntimeException
     */
    void updateTaskStatus(Task task, TaskStatusEnum status) throws RuntimeException;
}
