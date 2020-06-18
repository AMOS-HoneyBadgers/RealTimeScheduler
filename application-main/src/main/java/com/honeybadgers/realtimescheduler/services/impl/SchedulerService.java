package com.honeybadgers.realtimescheduler.services.impl;

import com.honeybadgers.communication.ICommunication;
import com.honeybadgers.models.model.*;
import com.honeybadgers.realtimescheduler.services.IGroupService;
import com.honeybadgers.realtimescheduler.services.ISchedulerService;
import com.honeybadgers.realtimescheduler.services.ITaskService;
import com.honeybadgers.redis.repository.LockRedisRepository;
import com.honeybadgers.redis.repository.TaskRedisRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.honeybadgers.models.model.ModeEnum.Sequential;

import static com.honeybadgers.models.model.Constants.*;

@Service
public class SchedulerService implements ISchedulerService {

    static final Logger logger = LogManager.getLogger(SchedulerService.class);

    @Value("${scheduler.trigger}")
    String scheduler_trigger;

    @Autowired
    TaskRedisRepository taskRedisRepository;

    @Autowired
    LockRedisRepository lockRedisRepository;

    @Autowired
    ITaskService taskService;

    @Autowired
    ICommunication sender;

    @Autowired
    IGroupService groupService;

    @Override
    public RedisTask createRedisTask(String taskId) {
        Task currentTask = taskService.getTaskById(taskId).orElse(null);
        if (currentTask == null)
            throw new RuntimeException("could not find task with id:" + taskId);

        RedisTask redisTask = new RedisTask();
        redisTask.setId(taskId);
        redisTask.setGroupid(currentTask.getGroup().getId());

        return redisTask;
    }

    @Override
    public List<RedisTask> getAllRedisTasksAndSort() {
        Iterable<RedisTask> redisTasks = taskRedisRepository.findAll();
        if(redisTasks == null)
            throw new RuntimeException("could not find any redisTasks in repo");

        List<RedisTask> sortedList = new ArrayList<RedisTask>();
        redisTasks.forEach(sortedList::add);
        Collections.sort(sortedList, (o1, o2) -> o1.getPriority() > o2.getPriority() ? -1 : (o1.getPriority() < o2.getPriority()) ? 1 : 0);

        return sortedList;
    }

    public int getLimitFromGroup(List<String> groupsOfTask, String grpId) {
        int minLimit = Integer.MAX_VALUE;

        for (String groupId : groupsOfTask) {
            Group currentGroup = groupService.getGroupById(groupId);
            if(currentGroup == null || currentGroup.getParallelismDegree() == null)
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
        RedisLock lock = lockRedisRepository.findById(lockId).orElse(null);
        return lock != null;
    }

    @Override
    public boolean isGroupLocked(String groupId) {
        if (groupId == null)
            throw new IllegalArgumentException("Method isGroupLocked: given groupId is null!");

        String lockId = LOCK_GROUP_PREFIX + groupId;
        RedisLock lock = lockRedisRepository.findById(lockId).orElse(null);
        return lock != null;
    }

    @Override
    public boolean isSchedulerLocked() {
        RedisLock lock = lockRedisRepository.findById(LOCK_SCHEDULER_ALIAS).orElse(null);
        return lock != null;
    }

    // TODO Transaction
    @Override
    public void scheduleTask(String taskId) {
        // Received special trigger from feedback -> sendTasks has to start immediately
        if(taskId.equals(scheduler_trigger)) {
            sendTaskstoDispatcher(this.getAllRedisTasksAndSort());
            return;
        }

        logger.info("Step 2: search for task in Redis DB with id: " + taskId);
        RedisTask redisTask = taskRedisRepository.findById(taskId).orElse(null);

        if (redisTask == null) {
            logger.debug("no task with id:" + taskId + " found in redis database, creating new");
            redisTask = createRedisTask(taskId);
        }

        Task task = taskService.getTaskById(taskId).orElse(null);
        if (task == null)
            throw new RuntimeException("task could not be found in database with id: " + taskId);

        logger.info("Step 3: calculate priority for id: " + taskId);
        redisTask.setPriority(taskService.calculatePriority(task));
        logger.info("redistask is: " + redisTask.toString());

        taskRedisRepository.save(redisTask);

        List<RedisTask> tasks = this.getAllRedisTasksAndSort();

        if (!isSchedulerLocked()) {
            logger.info("Step 4: try and send Tasks to dispatcher");
            sendTaskstoDispatcher(tasks);

        } else
            logger.info("Scheduler is locked!");
    }

    // TODO Transaction
    // TODO CHECK IF TASK WAS SENT TO DISPATCHER ALREADY
    public void sendTaskstoDispatcher(List<RedisTask> tasks) {
        try {
            for (int i = 0; i < tasks.size(); i++) {
                RedisTask currentTask = tasks.get(i);

                Task task = taskService.getTaskById(currentTask.getId()).orElse(null);
                if (task == null)
                    throw new RuntimeException("Task not found in Postgre Database for taskid: " + currentTask.getId());

                if(isTaskLocked(currentTask.getId())) {
                    logger.info("Task with id " + currentTask.getId() + " is currently paused!");
                    continue;
                }

                List<String> groupsOfTask = taskService.getRecursiveGroupsOfTask(currentTask.getId());

                if (checkGroupOrAncesterGroupIsOnPause(groupsOfTask))
                    continue;

                if (!checkIfTaskIsInActiveTime(task) || !checkIfTaskIsInWorkingDays(task) || sequentialHasToWait(task))
                    continue;

                String groupParlallelName = LOCK_GROUP_PREFIX_RUNNING_TASKS + currentTask.getGroupid();

                // Get Parlellism Current Task Amount from Database, if it doesnt exist, we initialize with 0
                RedisLock currentParallelismDegree = lockRedisRepository.findById(groupParlallelName).orElse(null);
                if (currentParallelismDegree == null)
                    currentParallelismDegree = createGroupParallelismTracker(groupParlallelName);

                int limit = getLimitFromGroup(groupsOfTask, currentTask.getGroupid());
                if (currentParallelismDegree.getCurrentTasks() >= limit)
                    continue;

                currentParallelismDegree.setCurrentTasks(currentParallelismDegree.getCurrentTasks() + 1);
                lockRedisRepository.save(currentParallelismDegree);
                logger.info("current_tasks for " + currentParallelismDegree.getId() + "is now at: " + currentParallelismDegree.getCurrentTasks());

                sender.sendTaskToDispatcher(currentTask.getId());
                logger.info("Sent task to dispatcher queue for taskid: " + currentTask.getId());

                taskRedisRepository.deleteById(currentTask.getId());
                logger.info("Deleted task from redis database for taskid: " + currentTask.getId());
            }
        } catch (IndexOutOfBoundsException e) {
            logger.error(e.getMessage());
        }
    }

    public boolean checkGroupOrAncesterGroupIsOnPause(List<String> groupsOfTask) {
        for (String groupId : groupsOfTask) {
            // check if group is paused (IllegalArgExc should not happen, because groupsOfTask was check on containing null values)
            if(isGroupLocked(groupId)) {
                logger.debug("Found paused group with groupId " + groupId);
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

            if (task.getIndexNumber() == parentgroup.getLastIndexNumber()+1)
                return false;
            else {
                logger.info("task with id: " + task.getId() + "is not sent due to Sequential");
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

        logger.info("task with id: " + task.getId() + "is not sent due to workingDays");
        return false;
    }

    public int[] getActualWorkingDaysForTask(Task task) {
        int[] workingDays = task.getWorkingDays();

        if(workingDays != null)
            return workingDays;

        if(task.getGroup() == null)
            throw new RuntimeException("parentgroup from " + task.getId() + " is null");

        Group parentGroup = groupService.getGroupById(task.getGroup().getId());

        while (parentGroup != null) {
            if (parentGroup.getWorkingDays() != null)
                return parentGroup.getWorkingDays();

            if(parentGroup.getParentGroup() == null)
                break;

            parentGroup = groupService.getGroupById(parentGroup.getParentGroup().getId());
        }

        return new int[]{1,1,1,1,1,1,1};
    }


    public RedisLock createGroupParallelismTracker(String id) {
        RedisLock curr = new RedisLock();
        curr.setId(id);
        return curr;
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
        } catch(ParseException pe) {
            logger.error(pe.getMessage());
        }
        logger.info("task with id: " + task.getId() + "is not sent due to ActiveTimes");
        return false;
    }

    public List<ActiveTimes> getActiveTimesForTask(Task task) {
        List<ActiveTimes> activeTimes = task.getActiveTimeFrames();
        if(activeTimes != null)
            return activeTimes;

        if(task.getGroup() == null)
            throw new RuntimeException("parentgroup from " + task.getId() + " is null");

        Group parentGroup = groupService.getGroupById(task.getGroup().getId());

        while (parentGroup != null) {
            if (parentGroup.getActiveTimeFrames() != null)
                return parentGroup.getActiveTimeFrames();

            if(parentGroup.getParentGroup() == null)
                break;

            parentGroup = groupService.getGroupById(parentGroup.getParentGroup().getId());
        }

        return new ArrayList<>();
    }
}
