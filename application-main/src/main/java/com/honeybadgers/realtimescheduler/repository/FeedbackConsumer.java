package com.honeybadgers.realtimescheduler.repository;

import com.honeybadgers.communication.ICommunication;
import com.honeybadgers.models.model.RedisLock;
import com.honeybadgers.models.model.Task;
import com.honeybadgers.models.model.Group;
import com.honeybadgers.models.model.ModeEnum;
import com.honeybadgers.realtimescheduler.services.impl.TaskService;
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

    @RabbitListener(queues = "dispatch.feedback", containerFactory = "feedbackcontainerfactory")
    public void receiveFeedbackFromDispatcher(String id) throws InterruptedException {
        // Race condition TODO Transaction
        // TODO HANDLE NEGATIVE FEEDBACK
        System.out.println("Step 5: Received feedback from dispatcher");

        Task currentTask = service.getTaskById(id).orElse(null);
        if(currentTask == null)
            throw new RuntimeException("could not find tasks in postgre database");

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

        //for sequential tasks we increase the indexnumber of the parentgroup by one
        //TODO is it necessary to do this also for all grandparent groups??
        if(currentTask.getModeEnum()== ModeEnum.Sequential){
            Group group = currentTask.getGroup();
            logger.info("update index Number of group"+group.getId());
            group.setLastIndexNumber(group.getLastIndexNumber()+1);
            groupPostgresRepository.save(group);
        }
        logger.info("Step 6: Decreased current_tasks is now at :" + currentParallelismDegree.getCurrentTasks());

        // TODO WHEN TO DELETE THE TASK FROM POSTGRE DATABASE
        // TODO send Event to Scheduler, so the workflow of scheduling etc. is beeing triggered in a new QUEUE atm just workaround
        sender.sendTaskToTasksQueue(scheduler_trigger);
        logger.info("Step 7: Send Trigger for Scheduler, so new Tasks can be send to Dispatcher");
    }
}
