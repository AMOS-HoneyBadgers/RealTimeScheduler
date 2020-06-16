package com.honeybadgers.realtimescheduler.services.impl;

import com.honeybadgers.communication.ICommunication;
import com.honeybadgers.models.*;
import com.honeybadgers.realtimescheduler.repository.LockRedisRepository;
import com.honeybadgers.realtimescheduler.repository.TaskRedisRepository;
import com.honeybadgers.realtimescheduler.services.IGroupService;
import com.honeybadgers.realtimescheduler.services.ISchedulerService;
import com.honeybadgers.realtimescheduler.services.ITaskService;
import org.apache.logging.log4j.Level;
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

import static com.honeybadgers.models.ModeEnum.Sequential;

@Service
public class SchedulerService implements ISchedulerService {

    static final Logger logger = LogManager.getLogger(SchedulerService.class);


    // These prefixes are for the case, that a group exists with id='SCHEDULER_LOCK_ALIAS'
    // HAVE TO BE THE SAME AS IN ManagementService IN managementapi!!!!!!!!!!!!! mach halt bitte noch mehr Ausrufezeichen
    public static final String LOCKREDIS_SCHEDULER_ALIAS = "SCHEDULER_LOCK_ALIAS";
    public static final String LOCKREDIS_TASK_PREFIX = "TASK:";
    public static final String LOCKREDIS_GROUP_PREFIX = "GROUP:";


    @Value("${scheduler.group.runningtasks}")
    String LOCKREDIS_GROUP_PREFIX_RUNNING_TASKS;

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
            throw new RuntimeException("could not find task with id:" + currentTask);

        RedisTask redisTask = new RedisTask();
        redisTask.setId(taskId);
        redisTask.setGroupid(currentTask.getGroup().getId());

        return redisTask;
    }

    @Override
    public List<RedisTask> getAllRedisTasksAndSort() {
        Iterable<RedisTask> redisTasks = taskRedisRepository.findAll();
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

        logger.info("limit is now at: " + minLimit);
        return minLimit;
    }

    @Override
    public boolean isTaskLocked(String taskId) {
        // TODO check if scheduler or any group (INCLUDING PARENTS!!!) is locked
        String lockId = LOCKREDIS_TASK_PREFIX + taskId;
        RedisLock lock = lockRedisRepository.findById(lockId).orElse(null);
        return lock != null;
    }

    @Override
    public boolean isGroupLocked(String groupId) {
        String lockId = LOCKREDIS_GROUP_PREFIX + groupId;
        RedisLock lock = lockRedisRepository.findById(lockId).orElse(null);
        return lock != null;
    }

    @Override
    public boolean isSchedulerLocked() {
        RedisLock lock = lockRedisRepository.findById(LOCKREDIS_SCHEDULER_ALIAS).orElse(null);
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
        logger.log(Level.INFO, tasks);

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
                String groupParlallelName = LOCKREDIS_GROUP_PREFIX_RUNNING_TASKS + currentTask.getGroupid();


                // Get Parlellism Current Task Amount from Database, if it doesnt exist, we initialize with 0
                RedisLock currentParallelismDegree = lockRedisRepository.findById(groupParlallelName).orElse(null);
                if (currentParallelismDegree == null)
                    currentParallelismDegree = createGroupParallelismTracker(groupParlallelName);

                // Get ActiveTimes for Task and check if it is allowed to be dispatched
                Task task = taskService.getTaskById(currentTask.getId()).orElse(null);
                logger.log(Level.INFO, "Task:");
                logger.log(Level.INFO, task.getId());
                if (task == null) {
                    throw new RuntimeException("Task not found in Postgre Database");
                }
                logger.log(Level.INFO, "checkIfTaskIsInActiveTime");
                if (!checkIfTaskIsInActiveTime(task))
                    continue;
                logger.log(Level.INFO, "checkIfTaskIsInWorkingDays");
                if (!checkIfTaskIsInWorkingDays(task))
                    continue;
                logger.log(Level.INFO, "sequentialCheck");
                if (sequentialHasToWait(task))
                    continue;
                // get Limit and compare if we are allowed to send new Tasks to Dispatcher
                logger.log(Level.INFO, "parallelismdegree");
                int limit = getLimitFromGroup(currentTask.getGroupid());
                if (currentParallelismDegree.getCurrentTasks() >= limit)
                    continue;
                logger.log(Level.INFO, "task should now be sent to dispatcher");
                // Task will be send to dispatcher, change currentTasks + 1
                currentParallelismDegree.setCurrentTasks(currentParallelismDegree.getCurrentTasks() + 1);
                logger.info("current_tasks is now increased to : " + currentParallelismDegree.getCurrentTasks());
                lockRedisRepository.save(currentParallelismDegree);

                logger.info("deleting task from redis database");
                taskRedisRepository.deleteById(currentTask.getId());

                sender.sendTaskToDispatcher(currentTask.getId());
            }
        } catch (IndexOutOfBoundsException e) {
            //in case inputtask list is smaller than foor loop size
            logger.info("in case inputtask list is smaller than foor loop size. Size:" + e.getMessage());
        }
    }

    public boolean sequentialHasToWait(Task task) {
        if (task.getModeEnum() == Sequential) {
            logger.info("task getIndexNumber " + task.getIndexNumber());
            Group parentgroup = task.getGroup();
            logger.info("parentgroup lastindexnumber " + parentgroup.getLastIndexNumber());
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
        if (workingdaybools.get(convertUtils.fitDayOfWeekToWorkingDayBools(dayofweek)))
            return true;
        return false;
    }

    public int[] getActualWorkingDaysForTask(Task task) {
        int[] workingDays = task.getWorkingDays();
        Group parentGroup = null;
        try {
            parentGroup = groupService.getGroupById(task.getGroup().getId());
        } catch (NullPointerException e) {
            logger.log(Level.INFO, "parentgroup from " + task.getId() + " is null \n" + e.getMessage());
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


    private RedisLock createGroupParallelismTracker(String id) {
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

        logger.log(Level.INFO, activeTimes);
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
        logger.log(Level.INFO, "activetimes");
        logger.log(Level.INFO, activeTimes);
        Group parentGroup = null;
        try {
            parentGroup = groupService.getGroupById(task.getGroup().getId());
        } catch (NullPointerException e) {
            logger.log(Level.INFO, "parentgroup from " + task.getId() + " is null \n" + e.getMessage());
        }
        if (parentGroup == null)
            return activeTimes;

        List<ActiveTimes> activeTimesTemp = parentGroup.getActiveTimeFrames();
        logger.log(Level.INFO, "parentactivetimes");
        logger.log(Level.INFO, activeTimesTemp);
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
