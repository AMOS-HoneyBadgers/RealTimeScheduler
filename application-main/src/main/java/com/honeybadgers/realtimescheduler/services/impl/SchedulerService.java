package com.honeybadgers.realtimescheduler.services.impl;

import com.honeybadgers.communication.ICommunication;
import com.honeybadgers.communication.model.TaskQueueModel;
import com.honeybadgers.models.model.*;
import com.honeybadgers.models.model.jpa.Group;
import com.honeybadgers.models.model.jpa.Paused;
import com.honeybadgers.models.model.jpa.Task;
import com.honeybadgers.models.model.jpa.TaskStatusEnum;
import com.honeybadgers.postgre.repository.GroupRepository;
import com.honeybadgers.postgre.repository.PausedRepository;
import com.honeybadgers.postgre.repository.TaskRepository;
import com.honeybadgers.realtimescheduler.services.IGroupService;
import com.honeybadgers.realtimescheduler.services.ILockService;
import com.honeybadgers.realtimescheduler.services.ISchedulerService;
import com.honeybadgers.realtimescheduler.services.ITaskService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.TransactionException;
import org.hibernate.exception.LockAcquisitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import static com.honeybadgers.models.model.Constants.*;
import static com.honeybadgers.models.model.jpa.ModeEnum.Sequential;

@Service
public class SchedulerService implements ISchedulerService {

    static final Logger logger = LogManager.getLogger(SchedulerService.class);
    static volatile boolean stopSchedulerDueToLockAcquisitionException = false;

    @Value("${scheduler.trigger}")
    String scheduler_trigger;

    @Autowired
    SchedulerService _self;

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    PausedRepository pausedRepository;

    @Autowired
    ITaskService taskService;

    @Autowired
    ICommunication sender;

    @Autowired
    IGroupService groupService;

    @Autowired
    GroupRepository groupRepository;

    @Autowired
    ConvertUtils convertUtils;

    @Autowired
    ILockService lockService;

    /**
     * Setter for static variable stopSchedulerDueToLockAcquisitionException
     *
     * @param value new value
     */
    public static void setStopSchedulerDueToLockAcquisitionException(boolean value) {
        stopSchedulerDueToLockAcquisitionException = value;
    }

    @Override
    public boolean isTaskPaused(String taskId) {
        if (taskId == null)
            throw new IllegalArgumentException("Method isTaskLocked: given taskId is null!");

        String pausedId = PAUSED_TASK_PREFIX + taskId;
        Paused paused = pausedRepository.findById(pausedId).orElse(null);
        return paused != null;
    }

    @Override
    public boolean isGroupPaused(String groupId) {
        if (groupId == null)
            throw new IllegalArgumentException("Method isGroupLocked: given groupId is null!");

        String pausedId = PAUSED_GROUP_PREFIX + groupId;
        Paused paused = pausedRepository.findById(pausedId).orElse(null);
        return paused != null;
    }

    @Override
    public boolean isSchedulerPaused() {
        Paused paused = pausedRepository.findById(PAUSED_SCHEDULER_ALIAS).orElse(null);
        return paused != null;
    }

    @Override
    public void scheduleTaskWrapper(String trigger) {
        SchedulerService.setStopSchedulerDueToLockAcquisitionException(false);

        Thread lockrefresherThread = null;
        try {

            // try to acquire scheduling lock from Lock Application
            LockResponse lockResponse = lockService.requestLock();

            // create and start lock-refresh-thread
            lockrefresherThread = lockService.createLockRefreshThread(lockResponse);
            lockrefresherThread.start();

            // get all tasks
            List<Task> waitingTasks;
            if (trigger.equals(scheduler_trigger))
                waitingTasks = taskRepository.findAllScheduledTasksSorted();
            else
                waitingTasks = taskRepository.findAllWaitingTasks();

            // schedule tasks
            logger.info("Step 2: scheduling " + waitingTasks.size() + " tasks");
            for (Task task : waitingTasks) {
                try {
                    if (stopSchedulerDueToLockAcquisitionException)
                        return;
                    _self.scheduleTask(task);
                } catch (CannotAcquireLockException | LockAcquisitionException exception) {
                    logger.warn("Task " + task.getId() + " Scheduling LockAcquisitionException!");
                } catch (JpaSystemException | TransactionException exception) {
                    logger.warn("Task " + task.getId() + " Scheduling TransactionException!");
                }
            }

            // dispatch tasks
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            // +1 because postgres arrays starts at 1
            int postgresIndex = convertUtils.fitDayOfWeekToWorkingDayBooleans(calendar.get(Calendar.DAY_OF_WEEK)) + 1;
            List<Task> tasks = taskRepository.getTasksToBeDispatched(postgresIndex);
            if (!isSchedulerPaused()) {
                logger.info("Step 3: dispatching " + tasks.size() + " tasks");
                for (Task task : tasks) {
                    try {
                        if (stopSchedulerDueToLockAcquisitionException)
                            return;

                        if (_self.checkTaskForDispatchingAndUpdate(task)) {
                            // TODO see known issues of docs/technology_decisions.md
                            // dispatch here because this only gets executed if transaction succeeds
                            dispatchTask(task);
                            logger.info("Task " + task.getId() + " was sent to dispatcher queue and status was set to 'Dispatched'");
                        }

                    } catch (CannotAcquireLockException | LockAcquisitionException exception) {
                        logger.warn("Task " + task.getId() + " Dispatching LockAcquisitionException!");
                    } catch (JpaSystemException | TransactionException exception) {
                        logger.warn("Task " + task.getId() + " Dispatching TransactionException!");
                    }
                }
            } else
                logger.info("Scheduler is locked!");

            // stop lock-refresh-thread
            lockrefresherThread.interrupt();
        } catch (Exception e) {
            logger.error(e.getMessage());
            // TODO (problem: lockAcquisition from LockService also triggers this) sender.sendTaskToTasksQueue("ERROR->Schedule");
        } finally {
            if (lockrefresherThread != null)
                lockrefresherThread.interrupt();
        }
    }

    /**
     * Schedule given task and update in DB (Running as transaction)
     *
     * @param task task to be scheduled
     */
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void scheduleTask(Task task) {
        task.setTotalPriority(taskService.calculatePriority(task));
        logger.info("Task " + task.getId() + " calculated total priority: " + task.getTotalPriority());
        taskService.updateTaskStatus(task, TaskStatusEnum.Scheduled);
        taskRepository.save(task);
    }

    /**
     * Tries to send given task to the dispatcher if the conditions for sending are met.
     *
     * @param currentTask task to be send to the dispatcher
     * @return return true if status was updated and dispatch is wanted
     */
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public boolean checkTaskForDispatchingAndUpdate(Task currentTask) {
        if (isTaskPaused(currentTask.getId())) {
            logger.info("Task " + currentTask.getId() + " is currently paused!");
            return false;
        }

        List<String> groupsOfTask = taskService.getRecursiveGroupsOfTask(currentTask.getId());

        if (checkGroupOrAncesterGroupIsOnPause(groupsOfTask, currentTask.getId()))
            return false;

        if (sequentialHasToWait(currentTask) || checkParallelismDegreeSurpassed(groupsOfTask, currentTask.getId()))
            return false;

        // Increment current parallelismDegree for all ancestors
        for (String group : groupsOfTask) {
            groupRepository.incrementCurrentParallelismDegree(group);
        }

        // TODO custom query
        taskService.updateTaskStatus(currentTask, TaskStatusEnum.Dispatched);
        taskRepository.save(currentTask);

        return true;
    }

    /**
     * Converts given task to TaskQueueModel and dispatches the task
     *
     * @param task task to be dispatched
     */
    public void dispatchTask(Task task) {
        TaskQueueModel taskQueueModel = new TaskQueueModel();
        taskQueueModel.setGroupId(task.getGroup().getId());
        taskQueueModel.setId(task.getId());
        taskQueueModel.setMetaData(task.getMetaData());
        taskQueueModel.setDispatched(Timestamp.from(Instant.now()));

        sender.sendTaskToDispatcher(taskQueueModel);
    }

    /**
     * Check if CurrentParallelismDegree + 1 is greater than ParallelismDegree among all ancestor groups.
     *
     * @param groups List of ids of all ancestors.
     * @param taskid Task id for logging.
     * @return true if ParallelismDegree is surpassed for any ancestor.
     * false otherwise
     */
    public boolean checkParallelismDegreeSurpassed(List<String> groups, String taskid) {
        for (String groupId : groups) {
            Group currentGroup = groupService.getGroupById(groupId).orElse(null);
            if (currentGroup == null)
                continue;
            if (currentGroup.getCurrentParallelismDegree() + 1 > currentGroup.getParallelismDegree()) {
                logger.info("Task " + taskid + " was not sent due to Parallelism degree");
                return true;
            }
        }
        return false;
    }

    /**
     * Checks groups with id in given list on paused
     *
     * @param groupsOfTask List of groupIds to be checked on paused
     * @param taskid       taskId of logging
     * @return true if one group is paused
     */
    public boolean checkGroupOrAncesterGroupIsOnPause(List<String> groupsOfTask, String taskid) {
        for (String groupId : groupsOfTask) {
            // check if group is paused (IllegalArgExc should not happen, because groupsOfTask was check on containing null values)
            if (isGroupPaused(groupId)) {
                logger.info("Task " + taskid + " is paused by Group " + groupId);
                return true;
            }
        }
        return false;
    }

    /**
     * Check if given task has to wait due to its sequence number
     *
     * @param task task to be checked
     * @return true if task has to wait
     */
    public boolean sequentialHasToWait(Task task) {
        if (task.getModeEnum() == Sequential) {
            logger.debug("task getIndexNumber " + task.getIndexNumber());
            Group parentgroup = task.getGroup();
            logger.debug("parentgroup lastindexnumber " + parentgroup.getLastIndexNumber());

            if (task.getIndexNumber() == parentgroup.getLastIndexNumber() + 1)
                return false;
            else {
                logger.info("Task " + task.getId() + " is not sent due to Sequential");
                return true;
            }
        }
        return false;
    }
}
