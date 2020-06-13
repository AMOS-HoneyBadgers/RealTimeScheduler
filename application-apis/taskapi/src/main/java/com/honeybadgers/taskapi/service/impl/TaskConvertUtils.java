package com.honeybadgers.taskapi.service.impl;

import com.honeybadgers.models.ActiveTimes;
import com.honeybadgers.models.Task;
import com.honeybadgers.taskapi.models.TaskModel;
import com.honeybadgers.taskapi.models.TaskModelActiveTimes;
import com.honeybadgers.taskapi.models.TaskModelMeta;
import com.honeybadgers.taskapi.service.ITaskConvertUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TaskConvertUtils implements ITaskConvertUtils {

    @Override
    public TaskModel taskJpaToRest(Task task) {
        if(task == null)
            return null;

        TaskModel taskmodel = new TaskModel();

        taskmodel.setId(UUID.fromString(task.getId()));
        taskmodel.setGroupId(task.getGroup().getId());
        taskmodel.setPriority(task.getPriority());
        taskmodel.setActiveTimes(activeTimesJpaToRest(task.getActiveTimeFrames()));
        taskmodel.setWorkingDays(intArrayToBoolList(task.getWorkingDays()));
        taskmodel.setStatus(TaskModel.StatusEnum.fromValue(task.getStatus().toString().toLowerCase()));
        taskmodel.setMode(TaskModel.ModeEnum.fromValue(task.getModeEnum().toString().toLowerCase()));
        taskmodel.setTypeFlag(TaskModel.TypeFlagEnum.fromValue(task.getTypeFlagEnum().toString().toLowerCase()));
        taskmodel.setForce(task.isForce());
        taskmodel.setRetries(task.getRetries());
        taskmodel.setPaused(task.isPaused());
        taskmodel.setIndexNumber(task.getIndexNumber());
        taskmodel.setDeadline(timestampJpaToRest(task.getDeadline()));
        taskmodel.setMeta(metaDataJpaToRest(task.getMetaData()));

        return taskmodel;
    }

    @Override
    public List<TaskModelActiveTimes> activeTimesJpaToRest(List<ActiveTimes> activeTimes) {
        if(activeTimes == null){
            return null;
        }else {
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
        if(data == null){
            return null;
        }else{
            List<TaskModelMeta> list = data.entrySet().stream().map(d -> {
                TaskModelMeta metaData = new TaskModelMeta();
                metaData.setKey(d.getKey());
                metaData.setValue(d.getValue());
                return metaData;
            }).collect(Collectors.toList());
            return list;
        }
    }
}
