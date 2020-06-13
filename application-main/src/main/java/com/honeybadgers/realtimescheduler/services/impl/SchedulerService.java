package com.honeybadgers.realtimescheduler.services.impl;

import com.honeybadgers.communication.ICommunication;
import com.honeybadgers.models.Group;
import com.honeybadgers.models.RedisLock;
import com.honeybadgers.models.RedisTask;
import com.honeybadgers.models.Task;
import com.honeybadgers.realtimescheduler.repository.GroupPostgresRepository;
import com.honeybadgers.realtimescheduler.repository.LockRedisRepository;
import com.honeybadgers.realtimescheduler.repository.TaskRedisRepository;
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


    @Value("${dispatcher.capacity.id}")
    String dispatcherCapacityId;

    @Value("${dispatcher.capacity}")
    String dispatcherCapacity;

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
    GroupPostgresRepository groupPostgresRepository;


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

        Group childGroup = groupPostgresRepository.findById(groupId).orElse(null);
        if(childGroup == null)
            throw new RuntimeException("no group found for id +" + groupId);

        minLimit = childGroup.getParallelismDegree();

        while(childGroup.getParentGroup() != null) {
            Group parentGroup = groupPostgresRepository.findById(childGroup.getParentGroup().getId()).orElse(null);
            if(parentGroup == null)
                break;

            minLimit = Math.min(minLimit, parentGroup.getParallelismDegree());
            childGroup = parentGroup;
        }

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
        try {
            UUID id = UUID.fromString(taskId);
            logger.info(id);
        } catch(Exception e) {
            logger.info(e.getMessage());
            return;
        }

        if(taskId.startsWith("Test"))
            return;


        //Special case: gets trigger from feedback -> TODO in new QUEUE
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
            System.out.println("Step 4: send Tasks to dispatcher");
            sendTaskstoDispatcher(tasks);

        } else
            logger.info("Scheduler is locked!");
    }

    public void sendTaskstoDispatcher(List<RedisTask> tasks) {
        try {
            for(int i = 0; i < Integer.parseInt(dispatcherCapacity); i++) {

                // TODO Transaction cause of Race conditon
                // TODO locks, activeTimes, workingDays, ...
                // TODO handle when dispatcher sends negative feedback
                // TODO CHECK IF TASK WAS SENT TO DISPATCHER ALREADY

                RedisTask currentTask = tasks.get(i);

                String groupParlallelName = "GROUP_PREFIX_PARLELLISM_CURRENT_TASKS_RUNNING_FOR_GROUP" + currentTask.getGroupid();

                RedisLock currentParallelismDegree = lockRedisRepository.findById(groupParlallelName).orElse(null);
                if(currentParallelismDegree == null)
                    createGroupParallelismTracker(groupParlallelName);

                // If there is no capacity, we wont send any tasks to dispatcher anymore
                if(capacity.getCapacity() < 1)
                    break;


                capacity.setCapacity(capacity.getCapacity()-1);
                lockRedisRepository.save(capacity);
                System.out.println("deleting task from redis database");

                taskRedisRepository.deleteById(currentTask.getId());

                sender.sendTaskToDispatcher(currentTask.getId());
            }
        } catch(IndexOutOfBoundsException e) {
            logger.info("passt scho" + e.getMessage());
        }
    }

    private void createGroupParallelismTracker(String id) {
        RedisLock curr = new RedisLock();
        curr.setId(id);
        lockRedisRepository.save(curr);
    }
}
