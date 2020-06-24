package com.honeybadgers.realtimescheduler.consumer;

import com.honeybadgers.communication.ICommunication;
import com.honeybadgers.models.model.RedisLock;
import com.honeybadgers.models.model.Task;
import com.honeybadgers.models.model.Group;
import com.honeybadgers.models.model.ModeEnum;
import com.honeybadgers.realtimescheduler.repository.GroupPostgresRepository;
import com.honeybadgers.realtimescheduler.services.impl.TaskService;
import com.honeybadgers.redis.repository.LockRedisRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static com.honeybadgers.models.model.Constants.LOCK_GROUP_PREFIX_RUNNING_TASKS;

@Component
@EnableRabbit
public class FeedbackConsumer {

    static final Logger logger = LogManager.getLogger(FeedbackConsumer.class);

    @Autowired
    public ICommunication sender;

    @Autowired
    public LockRedisRepository lockRedisRepository;

    @Autowired
    GroupPostgresRepository groupPostgresRepository;

    @Value("${scheduler.trigger}")
    String scheduler_trigger;

    @Autowired
    TaskService service;

    // TODO Transaction
    // TODO HANDLE NEGATIVE FEEDBACK
    // TODO WHEN TO DELETE THE TASK FROM POSTGRE DATABASE
    @RabbitListener(queues = "dispatch.feedback", containerFactory = "feedbackcontainerfactory")
    public void receiveFeedbackFromDispatcher(String id) throws InterruptedException {
        logger.info("Task " + id + " was processed by the dispatcher");

        Task currentTask = service.getTaskById(id).orElse(null);
        if(currentTask == null)
            throw new RuntimeException("could not find tasks in postgre database");

        checkAndSetParallelismDegree(id, currentTask);

        if(currentTask.getModeEnum()== ModeEnum.Sequential)
            checkAndSetSequentialAndIndexNumber(currentTask);

        sender.sendTaskToTasksQueue(scheduler_trigger);
    }

    //TODO is it necessary to do this also for all grandparent groups??
    public void checkAndSetSequentialAndIndexNumber(Task currentTask) {
        Group group = currentTask.getGroup();
        logger.debug("Task " + currentTask.getId() + " update index Number of group " + group.getId());
        group.setLastIndexNumber(group.getLastIndexNumber()+1);
        groupPostgresRepository.save(group);
    }

    public void checkAndSetParallelismDegree(String id, Task currentTask) {
        // Get Current Running Tasks from Redis Database, throw exception if it wasnt found
        String groupParlallelName = LOCK_GROUP_PREFIX_RUNNING_TASKS + currentTask.getGroup().getId();
        RedisLock currentParallelismDegree = lockRedisRepository.findById(groupParlallelName).orElse(null);
        if(currentParallelismDegree == null)
            throw new RuntimeException("no parlallelismdegree found in redis database for task:   " + id);

        // When the scheduler receives Feedback, the tasks is finished and current running tasks can be decreased by 1
        // is not allowed to be 0 according to datev requirements, check here
        if(currentParallelismDegree.getCurrentTasks() -1 >= 0 )
            currentParallelismDegree.setCurrentTasks(currentParallelismDegree.getCurrentTasks() - 1);
        else
            currentParallelismDegree.setCurrentTasks(0);

        lockRedisRepository.save(currentParallelismDegree);
    }
}
