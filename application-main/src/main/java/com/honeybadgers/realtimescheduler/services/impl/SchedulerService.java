package com.honeybadgers.realtimescheduler.services.impl;

import com.honeybadgers.communication.ICommunication;
import com.honeybadgers.models.Group;
import com.honeybadgers.models.RedisLock;
import com.honeybadgers.models.RedisTask;
import com.honeybadgers.models.Task;
import com.honeybadgers.realtimescheduler.model.GroupAncestorModel;
import com.honeybadgers.realtimescheduler.repository.GroupPostgresRepository;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

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
    public RedisTask createRedisTask(String taskId){
        Task currentTask = taskService.getTaskById(taskId).orElse(null);
        if(currentTask == null)
            throw new RuntimeException("could not find task with id:" + currentTask);

        RedisTask redisTask = new RedisTask();
        redisTask.setId(taskId);
        redisTask.setGroupid(currentTask.getGroup().getId());

        return redisTask;
    }

    @Override
    public List<RedisTask> getAllRedisTasksAndSort(){
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

        while(childGroup.getParentGroup() != null) {
            Group parentGroup = groupService.getGroupById(childGroup.getParentGroup().getId());
            if(parentGroup == null)
                break;

            minLimit = Math.min(minLimit, parentGroup.getParallelismDegree());
            childGroup = parentGroup;
        }

        logger.info("limit is now at: " + minLimit);
        return minLimit;
    }

    @Override
    public boolean isTaskLocked(String taskId) {
        if (taskId == null)
            throw new IllegalArgumentException("Given taskId was null!");
        String lockId = LOCKREDIS_TASK_PREFIX + taskId;
        RedisLock lock = lockRedisRepository.findById(lockId).orElse(null);
        return lock != null;
    }

    @Override
    public boolean isGroupLocked(String groupId) {
        if (groupId == null)
            throw new IllegalArgumentException("Given groupId was null!");
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

        if(redisTask == null){
            logger.info("no task found, creating new");
            redisTask = createRedisTask(taskId);
        }

        Task task = taskService.getTaskById(taskId).orElse(null);
        if(task == null)
            throw new RuntimeException("task could not be found in database");

        logger.info("Step 3: calculate priority with the Redis Task");
        logger.info("redistask is: " + redisTask.toString());
        redisTask.setPriority(taskService.calculatePriority(task));
        logger.info("prio was calculated, now at: + " + redisTask.getPriority());

        taskRedisRepository.save(redisTask);


        List<RedisTask> tasks = this.getAllRedisTasksAndSort();

        // TODO ASK DATEV WIE SCHNELL DIE ABGEARBEITET WERDEN
        if(!isSchedulerLocked()) {
            // scheduler not locked -> can send
            logger.info("Step 4: send Tasks to dispatcher");
            sendTaskstoDispatcher(tasks);

        } else
            logger.info("Scheduler is locked!");
    }

    public void sendTaskstoDispatcher(List<RedisTask> tasks) {
        try {
            for(int i = 0; i < 100; i++) {

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
                // get group with ancestors (IllegalArgExc not needed to be caught, because currentTask.getId() cannot be null)
                List<String> groupsOfTask = taskService.getRecursiveGroupsOfTask(currentTask.getId());

                // check groups on paused
                boolean pausedFound = false;
                for (String groupId : groupsOfTask) {
                    // check if group is paused (IllegalArgExc should not happen, because groupsOfTask was check on containing null values)
                    if(isGroupLocked(groupId)) {
                        // group is paused -> break inner loop for checking group on paused
                        pausedFound = true;
                        logger.info("Found paused group with groupId " + groupId);
                        break;
                    }
                }
                // paused found in inner loop -> continue outer loop
                if(pausedFound)
                    continue;

                String groupParlallelName = LOCKREDIS_GROUP_PREFIX_RUNNING_TASKS + currentTask.getGroupid();


                // Get Parlellism Current Task Amount from Database, if it doesnt exist, we initialize with 0
                RedisLock currentParallelismDegree = lockRedisRepository.findById(groupParlallelName).orElse(null);
                if(currentParallelismDegree == null)
                    currentParallelismDegree = createGroupParallelismTracker(groupParlallelName);


                // get Limit and compare if we are allowed to send new Tasks to Dispatcher
                int limit = getLimitFromGroup(currentTask.getGroupid());
                if(currentParallelismDegree.getCurrentTasks() >= limit) {
                    logger.info("limit for parallelism is reached, no more tasks for this group can be accepted by dispatcher");
                    continue;
                }


                // Task will be send to dispatcher, change currentTasks + 1
                currentParallelismDegree.setCurrentTasks(currentParallelismDegree.getCurrentTasks() + 1);
                logger.info("current_tasks is now increased to : " + currentParallelismDegree.getCurrentTasks());
                lockRedisRepository.save(currentParallelismDegree);

                logger.info("deleting task from redis database");
                // sending to queue
                sender.sendTaskToDispatcher(currentTask.getId());
                logger.info("Sent task to dispatcher queue");

                // delete from scheduling repository
                taskRedisRepository.deleteById(currentTask.getId());
                logger.info("Deleted task from redis database");
            }
        } catch(IndexOutOfBoundsException e) {
            logger.info("passt scho" + e.getMessage());
        }
    }

    private RedisLock createGroupParallelismTracker(String id) {
        RedisLock curr = new RedisLock();
        curr.setId(id);
        lockRedisRepository.save(curr);
        return curr;
    }
}
