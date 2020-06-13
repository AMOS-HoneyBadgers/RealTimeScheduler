package com.honeybadgers.taskapi.service;

import com.honeybadgers.models.ActiveTimes;
import com.honeybadgers.models.Task;
import com.honeybadgers.models.utils.IConvertUtils;
import com.honeybadgers.taskapi.models.TaskModel;
import com.honeybadgers.taskapi.models.TaskModelActiveTimes;
import com.honeybadgers.taskapi.models.TaskModelMeta;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;


public interface ITaskConvertUtils extends IConvertUtils {

     TaskModel taskJpaToRest(Task task);

     List<TaskModelActiveTimes> activeTimesJpaToRest(List<ActiveTimes> activeTimes);

     List<TaskModelMeta> metaDataJpaToRest(Map<String,String> data);
}
