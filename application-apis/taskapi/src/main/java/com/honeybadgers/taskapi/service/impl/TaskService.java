package com.honeybadgers.taskapi.service.impl;

import com.honeybadgers.models.*;
import com.honeybadgers.taskapi.models.TaskModel;
import com.honeybadgers.taskapi.models.TaskModelActiveTimes;
import com.honeybadgers.taskapi.models.TaskModelMeta;
import com.honeybadgers.taskapi.repository.GroupRepository;
import com.honeybadgers.taskapi.repository.TaskRepository;
import com.honeybadgers.taskapi.service.ITaskService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import com.honeybadgers.taskapi.exceptions.JpaException;

import java.sql.Timestamp;
import java.util.stream.Collectors;

@Service
public class TaskService implements ITaskService {

    static final Logger logger = LogManager.getLogger(TaskService.class);

    @Autowired
    GroupRepository groupRepository;

    @Autowired
    TaskRepository taskRepository;

    @Override
    public Task createTask(TaskModel restModel) throws JpaException, UnknownEnumException {
        Task newTask = new Task();
        newTask.setId(restModel.getId().toString());
        Group group = groupRepository.findById(restModel.getGroupId()).orElse(null);
        // foreign key is declared as NOT NULL -> throw JpaException now because it will be thrown on save(newTask) anyway
        if(group == null)
            throw new JpaException("Group not found!");
        newTask.setGroup(group);

        // set priority or default prio of group
        if(restModel.getPriority() != null)
            newTask.setPriority(restModel.getPriority());
        else
            newTask.setPriority(group.getPriority());

        // convert List<TaskModelActiveTimes> to List<ActiveTimes> or use default of group
        if(restModel.getActiveTimes() != null)
            newTask.setActiveTimeFrames(restModel.getActiveTimes().stream().map(TaskModelActiveTimes::getAsJpaModel).collect(Collectors.toList()));
        else
            newTask.setActiveTimeFrames(group.getActiveTimeFrames());

        // convert List<Boolean> to int[]
        if(restModel.getWorkingDays() != null) {
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
        if(restModel.getMode() != null)
            newTask.setModeEnum(ModeEnum.getFromString(restModel.getMode().getValue()));
        else
            // use group default
            newTask.setModeEnum(group.getModeEnum());
        if(restModel.getTypeFlag() != null)
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
        if(restModel.getDeadline() != null)
            newTask.setDeadline(Timestamp.valueOf(restModel.getDeadline().toLocalDateTime()));
        else
            newTask.setDeadline(group.getDeadline());

        // map List<TaskModelMeta> to Map<String, String>
        if(restModel.getMeta() != null)
            newTask.setMetaData(restModel.getMeta().stream().collect(Collectors.toMap(TaskModelMeta::getKey, TaskModelMeta::getValue)));

        try {
            taskRepository.save(newTask);
        } catch (DataIntegrityViolationException e) {
            if(e.getMessage() != null) {
                logger.error("DataIntegrityViolation while trying to add new Task: \n" + e.getMessage());
                if(e.getMessage().contains("primary")) {
                    throw new JpaException("Primary or unique constraint failed!");
                } else {
                    throw new JpaException(e.getMessage());
                }
            } else {
                // exception has no message (should not happen but just in case)
                logger.error("DataIntegrityViolation on save new task!");
                logger.error(e.getStackTrace());
                throw new JpaException("DataIntegrityViolation on save new task!");
            }
        }

        return newTask;
    }
}
