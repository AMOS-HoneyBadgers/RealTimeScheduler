package com.honeybadgers.realtimescheduler.services.impl;

import com.honeybadgers.communication.ICommunication;
import com.honeybadgers.models.model.*;
import com.honeybadgers.postgre.repository.GroupRepository;
import com.honeybadgers.postgre.repository.LockRepository;
import com.honeybadgers.postgre.repository.TaskRepository;
import com.honeybadgers.realtimescheduler.services.IGroupService;
import com.honeybadgers.realtimescheduler.services.ISchedulerService;
import com.honeybadgers.realtimescheduler.services.ITaskService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.exception.LockAcquisitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.honeybadgers.models.model.Constants.*;
import static com.honeybadgers.models.model.ModeEnum.Sequential;

@Service
public class SchedulerService implements ISchedulerService {

    static final Logger logger = LogManager.getLogger(SchedulerService.class);

    @Value("${scheduler.trigger}")
    String scheduler_trigger;

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    LockRepository lockRepository;

    @Autowired
    ITaskService taskService;

    @Autowired
    ICommunication sender;

    @Autowired
    IGroupService groupService;

    @Autowired
    GroupRepository groupRepository;

    public int getLimitFromGroup(List<String> groupsOfTask, String grpId) {
        int minLimit = Integer.MAX_VALUE;

        for (String groupId : groupsOfTask) {
            Group currentGroup = groupService.getGroupById(groupId);
            if (currentGroup == null || currentGroup.getParallelismDegree() == null)
                continue;

            minLimit = Math.min(minLimit, currentGroup.getParallelismDegree());
        }
        logger.debug("limit for groupid: " + grpId + "is now at: " + minLimit);
        return minLimit;
    }

    @Override
    public boolean isTaskLocked(String taskId) {
        if (taskId == null)
            throw new IllegalArgumentException("Method isTaskLocked: given taskId is null!");

        String lockId = LOCK_TASK_PREFIX + taskId;
        Lock lock = lockRepository.findById(lockId).orElse(null);
        return lock != null;
    }

    @Override
    public boolean isGroupLocked(String groupId) {
        if (groupId == null)
            throw new IllegalArgumentException("Method isGroupLocked: given groupId is null!");

        String lockId = LOCK_GROUP_PREFIX + groupId;
        Lock lock = lockRepository.findById(lockId).orElse(null);
        return lock != null;
    }

    @Override
    public boolean isSchedulerLocked() {
        Lock lock = lockRepository.findById(LOCK_SCHEDULER_ALIAS).orElse(null);
        return lock != null;
    }

    @Override
    public void scheduleTask() {
        try {
            List<Task> waitingTasks = taskRepository.findAllWaitingTasks();
            for (Task task : waitingTasks ) {
                task.setTotalPriority(taskService.calculatePriority(task));
                logger.info("Task " + task.getId() + " calculated total priority: " + task.getTotalPriority());
                task.setStatus(TaskStatusEnum.Scheduled);
                taskRepository.save(task);
            }
            List<Task> tasks = taskRepository.findAllScheduledTasksSorted();
            if (!isSchedulerLocked()) {
                sendTaskstoDispatcher(tasks);
            } else
                logger.info("Scheduler is locked!");
        } catch (LockAcquisitionException e) {
            logger.info("LockAcquisitionException caught -> will be tried again at some point");
        }
    }

    /**
     * Tries to send each task in the given list to the dispatcher if the conditions for sending (of each individual task) are met.
     * @Transactional here only just to be sure (should be already in transaction due to only being called by transactional method)
     * @param tasks List of tasks to be send to the dispatcher
     */
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void sendTaskstoDispatcher(List<Task> tasks) {
        for (Task currentTask : tasks) {
            if (isTaskLocked(currentTask.getId())) {
                logger.info("Task " + currentTask.getId() + " is currently paused!");
                continue;
            }

            List<String> groupsOfTask = taskService.getRecursiveGroupsOfTask(currentTask.getId());

            if (checkGroupOrAncesterGroupIsOnPause(groupsOfTask, currentTask.getId()))
                continue;

            if (!checkIfTaskIsInActiveTime(currentTask) || !checkIfTaskIsInWorkingDays(currentTask) || sequentialHasToWait(currentTask))
                continue;

            // Get Parlellism Current Task Amount from group of task (this also includes tasks of )
            Group parentGroup = currentTask.getGroup();

            int limit = getLimitFromGroup(groupsOfTask, parentGroup.getId());
            // TODO bug User Story 84 (documents, as mentioned in US, in documents channel of discord)
            if (parentGroup.getCurrentParallelismDegree() >= limit) {
                logger.info("Task " + currentTask.getId() + " was not sned due to parallelism limit for Group " + parentGroup.getId() + " is now at: " + parentGroup.getCurrentParallelismDegree());
                continue;
            }
            currentTask.setGroup(groupRepository.incrementCurrentParallelismDegree(parentGroup.getId()));

            //logger.debug("Task " + currentTask.getId() + " sent.");
            sender.sendTaskToDispatcher(currentTask.getId());

            currentTask.setStatus(TaskStatusEnum.Dispatched);
            taskRepository.save(currentTask);
            logger.info("Task " + currentTask.getId() + " was sent to dispatcher queue and removed from redis Database");
        }

    }

    public boolean checkGroupOrAncesterGroupIsOnPause(List<String> groupsOfTask, String taskid) {
        for (String groupId : groupsOfTask) {
            // check if group is paused (IllegalArgExc should not happen, because groupsOfTask was check on containing null values)
            if (isGroupLocked(groupId)) {
                logger.info("Task " + taskid + " is paused by Group " + groupId);
                return true;
            }
        }
        return false;
    }

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

    public boolean checkIfTaskIsInWorkingDays(Task task) {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        int dayofweek = calendar.get(Calendar.DAY_OF_WEEK);
        int[] workingdays = getActualWorkingDaysForTask(task);
        ConvertUtils convertUtils = new ConvertUtils();
        List<Boolean> workingdaybools = convertUtils.intArrayToBoolList(workingdays);

        if (workingdaybools.get(convertUtils.fitDayOfWeekToWorkingDayBooleans(dayofweek)))
            return true;

        logger.info("Task " + task.getId() + " is not sent due to workingDays");
        return false;
    }

    public int[] getActualWorkingDaysForTask(Task task) {
        int[] workingDays = task.getWorkingDays();

        if (workingDays != null)
            return workingDays;

        if (task.getGroup() == null)
            throw new RuntimeException("parentgroup from " + task.getId() + " is null");

        Group parentGroup = groupService.getGroupById(task.getGroup().getId());

        while (parentGroup != null) {
            if (parentGroup.getWorkingDays() != null)
                return parentGroup.getWorkingDays();

            if (parentGroup.getParentGroup() == null)
                break;

            parentGroup = groupService.getGroupById(parentGroup.getParentGroup().getId());
        }

        return new int[]{1, 1, 1, 1, 1, 1, 1};
    }

    public boolean checkIfTaskIsInActiveTime(Task task) {
        Date current = new Date();
        Date from = new Date();
        Date to = new Date();
        List<ActiveTimes> activeTimes = new ArrayList<ActiveTimes>();
        SimpleDateFormat parser = new SimpleDateFormat("HH:mm:ss");
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        try {
            current = parser.parse(dateTimeFormatter.format(LocalDateTime.now()));

            activeTimes = getActiveTimesForTask(task);

            if (activeTimes == null || activeTimes.isEmpty())
                return true;

            for (ActiveTimes activeTime : activeTimes) {
                from = parser.parse(activeTime.getFrom().toString());
                to = parser.parse(activeTime.getTo().toString());
                if (current.before(to) && current.after(from)) {
                    return true;
                }
            }
        } catch (ParseException pe) {
            logger.error(pe.getMessage());
        }
        logger.info("Task " + task.getId() + " is not sent due to ActiveTimes");
        return false;
    }

    public List<ActiveTimes> getActiveTimesForTask(Task task) {
        List<ActiveTimes> activeTimes = task.getActiveTimeFrames();
        if (activeTimes != null)
            return activeTimes;

        if (task.getGroup() == null)
            throw new RuntimeException("parentgroup from " + task.getId() + " is null");

        Group parentGroup = groupService.getGroupById(task.getGroup().getId());

        while (parentGroup != null) {
            if (parentGroup.getActiveTimeFrames() != null)
                return parentGroup.getActiveTimeFrames();

            if (parentGroup.getParentGroup() == null)
                break;

            parentGroup = groupService.getGroupById(parentGroup.getParentGroup().getId());
        }

        return new ArrayList<>();
    }
}
