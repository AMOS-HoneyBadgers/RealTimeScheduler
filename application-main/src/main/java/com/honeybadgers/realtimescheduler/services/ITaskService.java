package com.honeybadgers.realtimescheduler.services;

import com.honeybadgers.models.model.Task;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public interface ITaskService {

    List<Task> getAllTasks();

    Optional<Task> getTaskById(String id);

    /**
     * Gets the parent group of task with given taskId and ALL of its parents (recursively using tree of postgres)
     * For more information concerning the query see the javadoc of GroupAncestorRepository.getAllAncestorIdsFromGroup()
     * @param taskId taskId of which task all groups are wanted
     * @return List of the ids of all group and their ancestors
     */
    @Transactional(isolation = Isolation.SERIALIZABLE)
    List<String> getRecursiveGroupsOfTask(String taskId);

    void finishTask(Task task);

    void deleteTask(String id);

    long calculatePriority(Task task);
}
