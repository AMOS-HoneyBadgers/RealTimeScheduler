package com.honeybadgers.taskapi.service;

import com.honeybadgers.communication.model.TaskQueueModel;
import com.honeybadgers.models.model.jpa.ActiveTimes;
import com.honeybadgers.models.model.jpa.Task;
import com.honeybadgers.models.exceptions.UnknownEnumException;
import com.honeybadgers.models.utils.IConvertUtils;
import com.honeybadgers.models.exceptions.CreationException;
import com.honeybadgers.models.exceptions.JpaException;
import com.honeybadgers.taskapi.models.TaskModel;
import com.honeybadgers.taskapi.models.TaskModelActiveTimes;
import com.honeybadgers.taskapi.models.TaskModelMeta;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;


@Service
public interface ITaskConvertUtils extends IConvertUtils {


     /**
      * Convert a Task from JPA Model to Rest Model.
      * @param task JPA Task Model.
      * @return Task Object which can be sent via Rest.
      */
     TaskModel taskJpaToRest(Task task);

     /**
      * Convert a Task from Rest Model to JPA Model.
      * @param restModel TaskModel received via Rest.
      * @return Task Object which can be stored in the Database.
      * @throws JpaException Parent Group does not exist.
      * @throws CreationException Parent Group can only hold other Groups.
      * @throws UnknownEnumException
      */
     @Transactional(isolation = Isolation.SERIALIZABLE)
     Task taskRestToJpa(TaskModel restModel) throws JpaException, CreationException, UnknownEnumException;

     /**
      * Convert ActiveTimes Objects from JPA Model to Rest Model.
      * @param activeTimes List of ActiveTimes.
      * @return List of TaskModelActiveTimes
      */
     List<TaskModelActiveTimes> activeTimesJpaToRest(List<ActiveTimes> activeTimes);

     /**
      * Convert ActiveTimes Objects from Rest Model to JPA Model.
      * @param data List of TaskModelActiveTimes Objects.
      * @return List of ActiveTimes.
      */
     List<TaskModelMeta> metaDataJpaToRest(Map<String,String> data);

     /**
      * Convert TaskModel received from REST to TaskQueueModel to be sent to priority Queue
      * @param taskModel taskModel to be converted
      * @return TaskQueueModel representing given TaskModel
      */
     TaskQueueModel taskRestToQueue(TaskModel taskModel);
}
