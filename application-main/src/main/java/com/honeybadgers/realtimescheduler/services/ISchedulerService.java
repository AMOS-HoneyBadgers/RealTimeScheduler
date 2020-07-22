package com.honeybadgers.realtimescheduler.services;

import org.springframework.stereotype.Service;

@Service
public interface ISchedulerService {

    /**
     * Searches 'paused' table of database whether the table contains the lock for the task with the given taskId
     * The lock consists of the prefix defined Constants.LOCK_TASK_PREFIX
     *
     * @param taskId taskId of task to be searched for
     * @return true if task lock was found
     */
    boolean isTaskPaused(String taskId);

    /**
     * Searches 'paused' table of database whether the table contains the lock for the group with the given groupId
     * The lock consists of the prefix defined Constants.LOCK_GROUP_PREFIX
     *
     * @param groupId taskId of task to be searched for
     * @return true if group lock was found
     */
    boolean isGroupPaused(String groupId);

    /**
     * Searches 'paused' table of database whether the table contains the special scheduler_lock id
     * defined by Constants.LOCK_SCHEDULER_ALIAS
     *
     * @return true if scheduler lock was found
     */
    boolean isSchedulerPaused();

    /**
     * Wrapper for transactional methods, which schedules each task (in transaction)
     * and then dispatches all tasks (in transaction)
     *
     * @param trigger trigger type which indicates, which tasks have to be scheduled (for schedule or reschedule)
     */
    void scheduleTaskWrapper(String trigger);
}
