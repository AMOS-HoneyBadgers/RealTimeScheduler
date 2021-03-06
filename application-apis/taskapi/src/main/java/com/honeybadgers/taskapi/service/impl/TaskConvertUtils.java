package com.honeybadgers.taskapi.service.impl;

import com.honeybadgers.communication.model.TaskQueueModel;
import com.honeybadgers.models.exceptions.CreationException;
import com.honeybadgers.models.exceptions.JpaException;
import com.honeybadgers.models.exceptions.UnknownEnumException;
import com.honeybadgers.models.model.jpa.*;
import com.honeybadgers.postgre.repository.GroupRepository;
import com.honeybadgers.postgre.repository.TaskRepository;
import com.honeybadgers.taskapi.models.TaskModel;
import com.honeybadgers.taskapi.models.TaskModelActiveTimes;
import com.honeybadgers.taskapi.models.TaskModelMeta;
import com.honeybadgers.taskapi.service.ITaskConvertUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.honeybadgers.models.model.Constants.DEFAULT_GROUP_ID;

@Service
public class TaskConvertUtils implements ITaskConvertUtils {

    @Autowired
    TaskRepository taskRepository;
    @Autowired
    GroupRepository groupRepository;

    @Override
    public TaskModel taskJpaToRest(Task task) {
        if (task == null)
            return null;

        TaskModel taskmodel = new TaskModel();

        taskmodel.setId(task.getId());
        taskmodel.setGroupId(task.getGroup().getId());
        taskmodel.setPriority(task.getPriority());
        taskmodel.setActiveTimes(activeTimesJpaToRest(task.getActiveTimeFrames()));
        taskmodel.setWorkingDays(intArrayToBoolList(task.getWorkingDays()));
        taskmodel.setStatus(TaskModel.StatusEnum.fromValue(task.getStatus().toString().toLowerCase()));
        taskmodel.setMode(TaskModel.ModeEnum.fromValue(task.getModeEnum().toString().toLowerCase()));
        taskmodel.setTypeFlag(TaskModel.TypeFlagEnum.fromValue(task.getTypeFlagEnum().toString().toLowerCase()));
        taskmodel.setForce(task.isForce());
        taskmodel.setRetries(task.getRetries());
        taskmodel.setIndexNumber(task.getIndexNumber());
        taskmodel.setDeadline(timestampJpaToRest(task.getDeadline()));
        taskmodel.setMeta(metaDataJpaToRest(task.getMetaData()));
        if (task.getHistory() != null)
            taskmodel.setHistory(task.getHistory().stream().map(history -> (Object) history).collect(Collectors.toList()));

        return taskmodel;
    }

    @Override
    public Task taskRestToJpa(TaskModel restModel) throws JpaException, CreationException, UnknownEnumException {
        Task newTask = new Task();

        newTask.setId(restModel.getId());

        if (restModel.getGroupId() == null)
            restModel.setGroupId(DEFAULT_GROUP_ID);

        Group group = groupRepository.findById(restModel.getGroupId()).orElse(null);
        // foreign key is declared as NOT NULL -> throw JpaException now because it will be thrown on save(newTask) anyway
        if (group == null)
            throw new JpaException("Group not found!");
        else {
            List<Group> groupChildren = groupRepository.findAllByParentGroupId(group.getId());
            if (!groupChildren.isEmpty())
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

        // convert List<Boolean> to int[]
        if (restModel.getWorkingDays() != null) {
            newTask.setWorkingDays(restModel.getWorkingDays().stream().mapToInt(value -> {
                if (value == null)
                    return 1;
                // convert boolean to int
                return (value ? 1 : 0);
            }).toArray());
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

        newTask.setIndexNumber(restModel.getIndexNumber());
        // map OffsetDateTime to Timestamp or use group default
        if (restModel.getDeadline() != null)
            newTask.setDeadline(Timestamp.valueOf(restModel.getDeadline().toLocalDateTime()));
        else
            newTask.setDeadline(group.getDeadline());

        // map List<TaskModelMeta> to Map<String, String>
        if (restModel.getMeta() != null)
            newTask.setMetaData(restModel.getMeta().stream().collect(Collectors.toMap(TaskModelMeta::getKey, TaskModelMeta::getValue)));

        History history = new History();
        history.setStatus(TaskStatusEnum.Waiting.toString());
        history.setTimestamp(Timestamp.from(Instant.now()));
        List<History> histories = new ArrayList<>();
        histories.add(history);
        newTask.setHistory(histories);

        return newTask;
    }

    @Override
    public List<TaskModelActiveTimes> activeTimesJpaToRest(List<ActiveTimes> activeTimes) {
        if (activeTimes == null) {
            return null;
        } else {
            List<TaskModelActiveTimes> list = activeTimes.stream().map(t -> {
                TaskModelActiveTimes time = new TaskModelActiveTimes();
                time.setFrom(t.getFrom());
                time.setTo(t.getTo());
                return time;
            }).collect(Collectors.toList());
            return list;
        }
    }

    @Override
    public List<TaskModelMeta> metaDataJpaToRest(Map<String, String> data) {
        if (data == null) {
            return null;
        } else {
            List<TaskModelMeta> list = data.entrySet().stream().map(d -> {
                TaskModelMeta metaData = new TaskModelMeta();
                metaData.setKey(d.getKey());
                metaData.setValue(d.getValue());
                return metaData;
            }).collect(Collectors.toList());
            return list;
        }
    }

    @Override
    public TaskQueueModel taskRestToQueue(TaskModel taskModel) {
        TaskQueueModel taskQueueModel = new TaskQueueModel();
        taskQueueModel.setGroupId(taskModel.getGroupId());
        taskQueueModel.setId(taskModel.getId());
        if (taskModel.getMeta() != null)
            taskQueueModel.setMetaData(taskModel.getMeta().stream().collect(Collectors.toMap(TaskModelMeta::getKey, TaskModelMeta::getValue)));
        taskQueueModel.setDispatched(Timestamp.from(Instant.now()));

        return taskQueueModel;
    }
}
