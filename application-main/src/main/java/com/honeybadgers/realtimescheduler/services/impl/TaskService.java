package com.honeybadgers.realtimescheduler.services.impl;

import com.honeybadgers.models.model.*;
import com.honeybadgers.postgre.repository.GroupAncestorRepository;
import com.honeybadgers.postgre.repository.TaskRepository;
import com.honeybadgers.realtimescheduler.services.ITaskService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

@Service
public class TaskService implements ITaskService {

    static final Logger logger = LogManager.getLogger(TaskService.class);

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    GroupAncestorRepository groupAncestorRepository;

    @Value("${com.realtimescheduler.scheduler.priority.deadline-modifier}")
    double deadlineModifier;
    @Value("${com.realtimescheduler.scheduler.priority.prio-modifier}")
    double prioModifier;
    @Value("${com.realtimescheduler.scheduler.priority.realtime-modifier}")
    double realtimeModifier;
    @Value("${com.realtimescheduler.scheduler.priority.retries-modifier}")
    double retriesModifier;
    @Value("${com.realtimescheduler.scheduler.priority.const}")
    double constant;
    @Value("${com.realtimescheduler.scheduler.priority.deadline-bonus-base-prio-dependant}")
    boolean deadlineBaseDependant;

    @Override
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    @Override
    public Optional<Task> getTaskById(String id) {
        return taskRepository.findById(id);
    }

    @Override
    public List<String> getRecursiveGroupsOfTask(String taskId) {
        if(taskId == null)
            throw new IllegalArgumentException("taskId must not be null!");

        Task task = getTaskById(taskId).orElse(null);
        if(task == null)
            throw new NoSuchElementException("Task with taskId " + taskId + " not found!");

        if(task.getGroup() == null)
            throw new IllegalStateException("CRITICAL: found task with taskId " + taskId + " which has no group!");

        GroupAncestorModel ancestorModel = groupAncestorRepository.getAllAncestorIdsFromGroup(task.getGroup().getId()).orElse(null);
        if(ancestorModel == null)
            return new ArrayList<>();

        // Assert, that the ancestor model contains no null values
        if(ancestorModel.getId() == null || ancestorModel.getAncestors() == null)
            throw new IllegalStateException("AncestorModel received from repository contains null values for taskId: " + taskId);

        List<String> groups = new ArrayList<>(Collections.singletonList(ancestorModel.getId()));
        groups.addAll(Arrays.asList(ancestorModel.getAncestors()));

        // Assert, that list will not contain null
        if(groups.contains(null))
            throw new IllegalStateException("Ancestor list contains null values for taskId: " + taskId);

        logger.debug("Found " + groups.size() + " groups for taskId " + taskId);
        return groups;
    }

    @Override
    public void finishTask(Task task) {
        task.setStatus(TaskStatusEnum.Finished);
        taskRepository.save(task);
    }

    @Override
    public void deleteTask(String id) {
        this.taskRepository.deleteById(id);
    }

    public void updateTaskhistory(Task task, TaskStatusEnum status) throws RuntimeException{
        if(task.getHistory() == null)
            throw new RuntimeException("Task has no History Object");
        List<History> hist = task.getHistory();
        hist.add(new History(status.toString(), Timestamp.from(Instant.now())));
        task.setHistory(hist);
    }

    @Override
    public long calculatePriority(Task task) {
        double basePrio = task.getPriority();
        // also consider group priority if task has none
        if(basePrio == 0 && task.getGroup() != null)
            basePrio = task.getGroup().getPriority();
        Timestamp deadline = task.getDeadline();

        Date currentTime = new Date(System.currentTimeMillis());
        double deadlineFactor = deadline == null ? 0 : (constant * deadlineModifier)/(Math.abs(deadline.getTime() - currentTime.getTime()) / (1000.0 * 60.0));

        boolean realtime = task.getTypeFlagEnum() == TypeFlagEnum.Realtime;
        int retries = task.getRetries();

        double prioFactor = basePrio == 0 ? 0 : ((constant - basePrio) * prioModifier);
        double realtimeFactor = realtime ? constant/realtimeModifier : 0;
        double retriesFactor = (retries * constant)/retriesModifier;

        return (long) (deadlineFactor + prioFactor + realtimeFactor - retriesFactor);
    }
}
