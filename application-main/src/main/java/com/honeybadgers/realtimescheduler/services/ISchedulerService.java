package com.honeybadgers.realtimescheduler.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public interface ISchedulerService {

    /**
     * Searches 'paused' table of database whether the table contains the lock for the task with the given taskId
     * The lock consists of the prefix defined Constants.LOCK_TASK_PREFIX
     * @param taskId taskId of task to be searched for
     * @return true if task lock was found
     */
    boolean isTaskLocked(String taskId);

    /**
     * Searches 'paused' table of database whether the table contains the lock for the group with the given groupId
     * The lock consists of the prefix defined Constants.LOCK_GROUP_PREFIX
     * @param groupId taskId of task to be searched for
     * @return true if group lock was found
     */
    boolean isGroupLocked(String groupId);

    /**
     * Searches 'paused' table of database whether the table contains the special scheduler_lock id
     * defined by Constants.LOCK_SCHEDULER_ALIAS
     * @return true if scheduler lock was found
     */
    boolean isSchedulerLocked();

    /**
     * Schedule all waiting tasks and then call sendToDispatcher
     * Method running as @Transactional
     */
    @Transactional(isolation = Isolation.SERIALIZABLE)
    void scheduleTask();
}
