package com.honeybadgers.realtimescheduler.services.impl;

import com.honeybadgers.communication.model.TaskQueueModel;
import com.honeybadgers.models.model.jpa.Task;
import com.honeybadgers.models.utils.IConvertUtils;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;

@Service
public class ConvertUtils implements IConvertUtils {
    /**
     * Converts the weekday so that it starts at zero.
     * Following syntax: Weekday: <Input from Calendar.DAY_OF_WEEK> --> <Output>
     * Monday: 2 --> 0
     * Tuesday: 3 --> 1
     * Wednesday: 4 --> 2
     * Thursday: 5 --> 3
     * Friday: 6 --> 4
     * Saturday: 7 --> 5
     * Sunday: 1 --> 6
     *
     * @param dayofweek equals Calendar.DAY_OF_WEEK
     * @return the weekday with monday equals zero
     */
    public int fitDayOfWeekToWorkingDayBooleans(int dayofweek) {
        if (dayofweek > 7 || dayofweek < 1)
            throw new IllegalArgumentException("Calendar.DAY_OF_WEEK should be between 1 and 7, but it is " + dayofweek);

        return dayofweek == 1 ? 6 : dayofweek - 2;
    }

    /**
     * Converts given jpa task to the model, sent to the dispatcher
     *
     * @param task task object to be converted
     * @return TaskQueueModel representing the given task
     */
    public TaskQueueModel taskJpaToQueue(Task task) {
        if (task.getGroup() == null)
            throw new IllegalStateException("Given task with id " + task.getId() + " has no group!");

        TaskQueueModel taskQueueModel = new TaskQueueModel();
        taskQueueModel.setGroupId(task.getGroup().getId());
        taskQueueModel.setId(task.getId());
        taskQueueModel.setMetaData(task.getMetaData());
        taskQueueModel.setDispatched(Timestamp.from(Instant.now()));

        return taskQueueModel;
    }
}