package com.honeybadgers.realtimescheduler.services.impl;

import com.honeybadgers.communication.ICommunication;
import com.honeybadgers.models.model.*;
import com.honeybadgers.realtimescheduler.repository.LockRedisRepository;
import com.honeybadgers.realtimescheduler.repository.TaskRedisRepository;
import com.honeybadgers.realtimescheduler.services.IGroupService;
import com.honeybadgers.realtimescheduler.services.ISchedulerService;
import com.honeybadgers.realtimescheduler.services.ITaskService;
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

    public int getLimitFromGroup(String groupId) {
        int minLimit;

        Group childGroup = groupService.getGroupById(groupId);
        if(childGroup == null || childGroup.getParallelismDegree() == null)
            throw new RuntimeException("no group or parlellismdegree found for id +" + groupId);

        minLimit = childGroup.getParallelismDegree();

        while (childGroup.getParentGroup() != null) {
            Group parentGroup = groupService.getGroupById(childGroup.getParentGroup().getId());
            if (parentGroup == null)
                break;

            minLimit = Math.min(minLimit, parentGroup.getParallelismDegree());
            childGroup = parentGroup;
        }

        logger.debug("limit for groupid: " + groupId + "is now at: " + minLimit);
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

    @Override
    public void scheduleTask(String taskId) {
        //Special case: gets trigger from feedback -> TODO in new QUEUE if necessary
        if(taskId.equals(scheduler_trigger)) {
            sendTaskstoDispatcher(this.getAllRedisTasksAndSort());
            return;
        }

        // TODO Transaction
        logger.info("Step 2: search for task in Redis DB");
        RedisTask redisTask = taskRedisRepository.findById(taskId).orElse(null);

        if (redisTask == null) {
            logger.info("no task found, creating new");
            redisTask = createRedisTask(taskId);
        }

        Task task = taskService.getTaskById(taskId).orElse(null);
        if (task == null)
            throw new RuntimeException("task could not be found in database");

        logger.info("Step 3: calculate priority with the Redis Task");
        logger.info("redistask is: " + redisTask.toString());
        redisTask.setPriority(taskService.calculatePriority(task));
        logger.info("prio was calculated, now at: + " + redisTask.getPriority());

        taskRedisRepository.save(redisTask);


        List<RedisTask> tasks = this.getAllRedisTasksAndSort();
        logger.info(tasks);

        // TODO ASK DATEV WIE SCHNELL DIE ABGEARBEITET WERDEN
        if (!isSchedulerLocked()) {
            // scheduler not locked -> can send
            logger.info("Step 4: send Tasks to dispatcher");
            sendTaskstoDispatcher(tasks);

        } else
            logger.info("Scheduler is locked!");
    }

    public void sendTaskstoDispatcher(List<RedisTask> tasks) {
        try {
            // TODO: Change this to the size of the tasks list
            for (int i = 0; i < 100; i++) {

                // TODO Transaction cause of Race conditon
                // TODO locks, activeTimes, workingDays, ...
                // TODO handle when dispatcher sends negative feedback
                // TODO CHECK IF TASK WAS SENT TO DISPATCHER ALREADY

                RedisTask currentTask = tasks.get(i);

                logger.info("Checking task and groups on paused.");
                // check if task is paused
                if(isTaskLocked(currentTask.getId())) {
                    logger.info("Task with id " + currentTask.getId() + " is currently paused!");
                    continue;
                }

                // TODO BITTE AUSLAGERN DIE METHOD WIRD HIER VIEL ZU LANG
                // get group with ancestors (IllegalArgExc not needed to be caught, because currentTask.getId() cannot be null)
                List<String> groupsOfTask = taskService.getRecursiveGroupsOfTask(currentTask.getId());

                // check groups on paused
                boolean pausedFound = false;
                for (String groupId : groupsOfTask) {
                    // check if group is paused (IllegalArgExc should not happen, because groupsOfTask was check on containing null values)
                    if(isGroupLocked(groupId)) {
                        // group is paused -> break inner loop for checking group on paused
                        pausedFound = true;
                        logger.debug("Found paused group with groupId " + groupId);
                        break;
                    }
                }
                // paused found in inner loop -> continue outer loop
                if(pausedFound)
                    continue;

                // TODO refactor zusammen legen mit group lock checker
                logger.info("Checking parallelism degree.");
                String groupParlallelName = LOCK_GROUP_PREFIX_RUNNING_TASKS + currentTask.getGroupid();

                // Get Parlellism Current Task Amount from Database, if it doesnt exist, we initialize with 0
                RedisLock currentParallelismDegree = lockRedisRepository.findById(groupParlallelName).orElse(null);
                if (currentParallelismDegree == null)
                    currentParallelismDegree = createGroupParallelismTracker(groupParlallelName);

                // Get ActiveTimes for Task and check if it is allowed to be dispatched
                Task task = taskService.getTaskById(currentTask.getId()).orElse(null);
                if (task == null) {
                    throw new RuntimeException("Task not found in Postgre Database");
                }
                logger.debug("checkIfTaskIsInActiveTime");
                if (!checkIfTaskIsInActiveTime(task))
                    continue;
                logger.debug("checkIfTaskIsInWorkingDays");
                if (!checkIfTaskIsInWorkingDays(task))
                    continue;
                logger.debug("sequentialCheck");
                if (sequentialHasToWait(task))
                    continue;
                // get Limit and compare if we are allowed to send new Tasks to Dispatcher
                logger.debug("parallelismdegree");
                int limit = getLimitFromGroup(currentTask.getGroupid());
                if (currentParallelismDegree.getCurrentTasks() >= limit)
                    continue;
                logger.info("task should now be sent to dispatcher");
                // Task will be send to dispatcher, change currentTasks + 1
                currentParallelismDegree.setCurrentTasks(currentParallelismDegree.getCurrentTasks() + 1);
                logger.info("current_tasks is now increased to : " + currentParallelismDegree.getCurrentTasks());
                lockRedisRepository.save(currentParallelismDegree);

                // sending to queue
                sender.sendTaskToDispatcher(currentTask.getId());
                logger.info("Sent task to dispatcher queue");

                // delete from scheduling repository
                taskRedisRepository.deleteById(currentTask.getId());
                logger.info("Deleted task from redis database");
            }
        } catch (IndexOutOfBoundsException e) {
            //in case inputtask list is smaller than foor loop size
            logger.info("in case inputtask list is smaller than foor loop size. Size:" + e.getMessage());
        }
    }

    public boolean sequentialHasToWait(Task task) {
        if (task.getModeEnum() == Sequential) {
            logger.debug("task getIndexNumber " + task.getIndexNumber());
            Group parentgroup = task.getGroup();
            logger.debug("parentgroup lastindexnumber " + parentgroup.getLastIndexNumber());
            if (task.getIndexNumber() == parentgroup.getLastIndexNumber()+1)
                return false;
            else
                return true;
        }
        return false;
    }

    public boolean checkIfTaskIsInWorkingDays(Task task) {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        int dayofweek = calendar.get(Calendar.DAY_OF_WEEK);
        ConvertUtils convertUtils = new ConvertUtils();
        int[] workingdays = getActualWorkingDaysForTask(task);
        List<Boolean> workingdaybools = convertUtils.intArrayToBoolList(workingdays);
        if (workingdaybools.get(convertUtils.fitDayOfWeekToWorkingDayBooleans(dayofweek)))
            return true;
        return false;
    }

    public int[] getActualWorkingDaysForTask(Task task) {
        int[] workingDays = task.getWorkingDays();
        //TODO: Refactor to if else
        Group parentGroup = null;
        try {
            parentGroup = groupService.getGroupById(task.getGroup().getId());
        } catch (NullPointerException e) {
            logger.debug("parentgroup from " + task.getId() + " is null \n" + e.getMessage());
        }
        if (parentGroup == null)
            return workingDays;

        int[] workingDaysTemp = parentGroup.getWorkingDays();
        if (workingDaysTemp != null) {
            workingDays = workingDaysTemp;
        }

        while (parentGroup.getParentGroup() != null) {
            parentGroup = groupService.getGroupById(parentGroup.getParentGroup().getId());
            if (parentGroup == null)
                break;

            workingDaysTemp = parentGroup.getWorkingDays();
            if (workingDaysTemp != null) {
                workingDays = workingDaysTemp;
            }
        }
        return workingDays;
    }


    public RedisLock createGroupParallelismTracker(String id) {
        RedisLock curr = new RedisLock();
        curr.setId(id);
        lockRedisRepository.save(curr);
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
        } catch (ParseException e) {
            e.printStackTrace();
        }
        activeTimes = getActiveTimesForTask(task);

        logger.info(activeTimes);
        if (activeTimes == null || activeTimes.isEmpty()) {
            return true;
        }
        for (ActiveTimes activeTime : activeTimes) {
            try {
                from = parser.parse(activeTime.getFrom().toString());
                to = parser.parse(activeTime.getTo().toString());
                if (current.before(to) && current.after(from)) {
                    return true;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
        return false;
    }

    public List<ActiveTimes> getActiveTimesForTask(Task task) {

        List<ActiveTimes> activeTimes = task.getActiveTimeFrames();
        logger.debug(activeTimes);
        //TODO: Refactor to if else
        Group parentGroup = null;
        try {
            parentGroup = groupService.getGroupById(task.getGroup().getId());
        } catch (NullPointerException e) {
            logger.info("parentgroup from " + task.getId() + " is null \n" + e.getMessage());
        }
        if (parentGroup == null)
            return activeTimes;

        List<ActiveTimes> activeTimesTemp = parentGroup.getActiveTimeFrames();
        logger.debug(activeTimesTemp);
        if (activeTimesTemp != null && !(activeTimesTemp.isEmpty())) {
            activeTimes = activeTimesTemp;
        }

        while (parentGroup.getParentGroup() != null) {
            parentGroup = groupService.getGroupById(parentGroup.getParentGroup().getId());
            if (parentGroup == null)
                break;

            activeTimesTemp = parentGroup.getActiveTimeFrames();
            if (activeTimesTemp != null && !(activeTimesTemp.isEmpty())) {
                activeTimes = activeTimesTemp;
            }
        }
        return activeTimes;

    }
}
