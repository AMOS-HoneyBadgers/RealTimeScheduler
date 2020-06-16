package com.honeybadgers.realtimescheduler.services.impl;

import com.honeybadgers.models.model.Task;
import com.honeybadgers.realtimescheduler.model.GroupAncestorModel;
import com.honeybadgers.realtimescheduler.repository.GroupAncestorRepository;
import com.honeybadgers.realtimescheduler.repository.TaskPostgresRepository;
import com.honeybadgers.realtimescheduler.services.ITaskService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.*;

@Service
public class TaskService implements ITaskService {

    static final Logger logger = LogManager.getLogger(TaskService.class);

    @Autowired
    TaskPostgresRepository taskPostgresRepository;

    @Autowired
    GroupAncestorRepository groupAncestorRepository;

    @Value("${com.realtimescheduler.scheduler.priority.deadline-modifier}")
    double deadlineModifier;

    @Value("${com.realtimescheduler.scheduler.priority.deadline-bonus-base-prio-dependant}")
    boolean deadlineBaseDependant;

    @Override
    public List<Task> getAllTasks() {
        return taskPostgresRepository.findAll();
    }

    @Override
    public Optional<Task> getTaskById(String id) {
        return taskPostgresRepository.findById(id);
    }

    @Override
    @Transactional
    public List<String> getRecursiveGroupsOfTask(String taskId) {
        // TODO transactions
        if(taskId == null)
            throw new IllegalArgumentException("taskId must not be null!");
        Task task = getTaskById(taskId).orElse(null);
        if(task == null)
            throw new NoSuchElementException("Task with taskId " + taskId + " not found!");
        // just for savety: check that group is not null (THIS SHOULD NEVER HAPPEN, because DB enforces task.group IS NOT NULL)
        if(task.getGroup() == null)
            throw new IllegalStateException("CRITICAL: found task with taskId " + taskId + " which has no group -> THIS SHOULD NOT HAVE HAPPENED (DB enforces this)!");

        GroupAncestorModel ancestorModel = groupAncestorRepository.getAllAncestorIdsFromGroup(task.getGroup().getId()).orElse(null);
        if(ancestorModel == null) {
            logger.info("Found no ancestor model for group with groupId " + task.getGroup().getId());
            return new ArrayList<>();
        }
        // assert, that the ancestor model contains no null values
        if(ancestorModel.getId() == null || ancestorModel.getAncestors() == null)
            throw new IllegalStateException("AncestorModel received from repository contains null values!");

        List<String> groups = new ArrayList<>(Collections.singletonList(ancestorModel.getId()));
        groups.addAll(Arrays.asList(ancestorModel.getAncestors()));

        // assert, that list will not contain null
        if(groups.contains(null))
            throw new IllegalStateException("Ancestor list contains null values!");
        logger.info("Found " + groups.size() + " groups/ancestors for taskId " + taskId);
        return groups;
    }

    @Override
    public void uploadTask(Task task) {
        taskPostgresRepository.save(task);
    }

    @Override
    public void deleteTask(String id) {
        this.taskPostgresRepository.deleteById(id);
    }

    @Override
    public long calculatePriority(Task task) {
        double finalPriority = 0;
        finalPriority = task.getPriority();
        Timestamp deadline = task.getDeadline();
        if(deadline != null){
            Date currentTime = new Date(System.currentTimeMillis());
            //timeDiff in Minuten umrechnen, da sonst Differenz zu klein (finalPriority ändert zu wenig)
            double timeDiff = Math.abs(deadline.getTime() - currentTime.getTime()) / (1000.0 * 60.0);
            logger.info("timediff: " + timeDiff);
            if(deadlineBaseDependant) {
                // the higher the base-prio, the higher it will be increased by same timediff
                finalPriority += ((deadlineModifier * finalPriority) / timeDiff);
            } else {
                finalPriority += (deadlineModifier/timeDiff);
            }
        }
        return Math.round(finalPriority);
    }

}
