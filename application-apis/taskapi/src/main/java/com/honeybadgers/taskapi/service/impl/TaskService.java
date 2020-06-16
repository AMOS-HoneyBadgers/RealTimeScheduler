package com.honeybadgers.taskapi.service.impl;

import com.honeybadgers.communication.ICommunication;
import com.honeybadgers.communication.model.TaskQueueModel;
import com.honeybadgers.models.model.*;
import com.honeybadgers.taskapi.exceptions.CreationException;
import com.honeybadgers.taskapi.models.TaskModel;
import com.honeybadgers.taskapi.models.TaskModelMeta;
import com.honeybadgers.taskapi.repository.GroupRepository;
import com.honeybadgers.taskapi.repository.TaskRepository;
import com.honeybadgers.taskapi.service.ITaskConvertUtils;
import com.honeybadgers.taskapi.service.ITaskService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import com.honeybadgers.taskapi.exceptions.JpaException;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TaskService implements ITaskService {

    static final Logger logger = LogManager.getLogger(TaskService.class);

    @Autowired
    GroupRepository groupRepository;

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    ICommunication sender;

    @Autowired
    ITaskConvertUtils converter;

    @Override
    public List<TaskModel> getAllTasks() {
        List<TaskModel> taskModelList;
       // Pageable PageXwithTwentyElements = PageRequest.of(0, 20);
        //taskRepository.findAll(PageXwithTwentyElements);
        List<Task> taskList = taskRepository.findAll();

        taskModelList = taskList.stream().map(t -> {
            TaskModel restModel = converter.taskJpaToRest(t);
            return restModel;
        }).collect(Collectors.toList());

        return taskModelList;
    }

    @Override
    public Task createTask(TaskModel restModel) throws JpaException, UnknownEnumException, CreationException {
        Task newTask = new Task();

        Task checkTask = taskRepository.findById(restModel.getId().toString()).orElse(null);
        if( checkTask != null ){
            throw new JpaException("Primary or unique constraint failed!");
        }

        newTask.setId(restModel.getId().toString());
        Group group = groupRepository.findById(restModel.getGroupId()).orElse(null);
        // foreign key is declared as NOT NULL -> throw JpaException now because it will be thrown on save(newTask) anyway
        if (group == null)
            throw new JpaException("Group not found!");
        else {
            List<Group> groupChildren = groupRepository.findAllByParentGroupId(group.getId());
            if (!groupChildren.isEmpty())
                // TODO perhaps move group of task or sth similar
                throw new CreationException("Group of task has other groups as children: " +
                        groupChildren.stream().map(Group::getId).collect(Collectors.joining(", ")) +
                        " -> aborting!");
        }
        newTask.setGroup(group);

        // set priority or default prio of group
        if (restModel.getPriority() != null)
            newTask.setPriority(restModel.getPriority());
        else
            newTask.setPriority(group.getPriority());

        // convert List<TaskModelActiveTimes> to List<ActiveTimes> or use default of group
        if (restModel.getActiveTimes() != null)
            newTask.setActiveTimeFrames(restModel.getActiveTimes().stream().map(taskModelActiveTimes -> taskModelActiveTimes.getAsJpaModel()).collect(Collectors.toList()));
        else
            newTask.setActiveTimeFrames(group.getActiveTimeFrames());

        // convert List<Boolean> to int[]
        if (restModel.getWorkingDays() != null) {
            newTask.setWorkingDays(restModel.getWorkingDays().stream().mapToInt(value -> {
                if (value == null)
                    return 1;
                // convert boolean to int
                return (value ? 1 : 0);
            }).toArray());
        } else {
            newTask.setWorkingDays(group.getWorkingDays());
        }

        // convert Enums using RestEnum.toString and JpaEnum.fromString
        newTask.setStatus(TaskStatusEnum.getFromString(restModel.getStatus().getValue()));
        if (restModel.getMode() != null)
            newTask.setModeEnum(ModeEnum.getFromString(restModel.getMode().getValue()));
        else
            // use group default
            newTask.setModeEnum(group.getModeEnum());
        if (restModel.getTypeFlag() != null)
            newTask.setTypeFlagEnum(TypeFlagEnum.getFromString(restModel.getTypeFlag().getValue()));
        else
            // use group default
            newTask.setTypeFlagEnum(group.getTypeFlagEnum());

        // parameters, which have default values defined
        newTask.setForce(restModel.getForce());
        newTask.setRetries(restModel.getRetries());
        newTask.setPaused(restModel.getPaused());

        newTask.setIndexNumber(restModel.getIndexNumber());
        // map OffsetDateTime to Timestamp or use group default
        if (restModel.getDeadline() != null)
            newTask.setDeadline(Timestamp.valueOf(restModel.getDeadline().toLocalDateTime()));
        else
            newTask.setDeadline(group.getDeadline());

        // map List<TaskModelMeta> to Map<String, String>
        if (restModel.getMeta() != null)
            newTask.setMetaData(restModel.getMeta().stream().collect(Collectors.toMap(TaskModelMeta::getKey, TaskModelMeta::getValue)));

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
        if (task.getDeadline() != null)
            taskQueueModel.setDeadline(Timestamp.valueOf(task.getDeadline().toLocalDateTime()));
        taskQueueModel.setGroupId(task.getGroupId());
        taskQueueModel.setId(task.getId().toString());
        taskQueueModel.setIndexNumber(task.getIndexNumber());
        if (task.getMeta() != null)
            taskQueueModel.setMetaData(task.getMeta().stream().collect(Collectors.toMap(TaskModelMeta::getKey, TaskModelMeta::getValue)));
        taskQueueModel.setPriority(task.getPriority());
        taskQueueModel.setRetries(task.getRetries());
        taskQueueModel.setTypeFlagEnum(task.getTypeFlag().getValue());
        sender.sendTaskToPriorityQueue(taskQueueModel);
    }

}
