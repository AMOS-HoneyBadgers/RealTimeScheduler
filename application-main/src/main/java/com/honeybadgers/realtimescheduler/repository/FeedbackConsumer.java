package com.honeybadgers.realtimescheduler.repository;

import com.honeybadgers.communication.ICommunication;
import com.honeybadgers.models.RedisLock;
import com.honeybadgers.models.Task;
import com.honeybadgers.realtimescheduler.services.impl.TaskService;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@EnableRabbit
public class FeedbackConsumer {

    @Autowired
    public ICommunication sender;

    @Autowired
    public LockRedisRepository lockRedisRepository;

    @Value("${scheduler.trigger}")
    String scheduler_trigger;

    @Value("${scheduler.group.runningtasks}")
    String LOCKREDIS_GROUP_PREFIX_RUNNING_TASKS;

    @Autowired
    TaskService service;

    @RabbitListener(queues = "dispatch.feedback", containerFactory = "feedbackcontainerfactory")
    public void receiveFeedbackFromDispatcher(String message) throws InterruptedException {
        // Race condition TODO Transaction
        // TODO HANDLE NEGATIVE FEEDBACK
        System.out.println("Step 5: Received feedback from dispatcher");

        Task currentTask = service.getTaskById(message).orElse(null);
        if(currentTask == null)
            throw new RuntimeException("could not find tasks in postgre database");

        // Get Current Running Tasks from Redis Database, throw exception if it wasnt found
        String groupParlallelName = LOCKREDIS_GROUP_PREFIX_RUNNING_TASKS + currentTask.getGroup().getId();
        RedisLock currentParallelismDegree = lockRedisRepository.findById(groupParlallelName).orElse(null);
        if(currentParallelismDegree == null)
            throw new RuntimeException("no parlallelismdegree found in redis database for task:   " + message);

        // When the scheduler receives Feedback, the tasks is finished and current running tasks can be decreased by 1
        // is not allowed to be 0 according to datev requirements, check here
        if(currentParallelismDegree.getCurrentTasks() -1 >= 0 )
            currentParallelismDegree.setCurrentTasks(currentParallelismDegree.getCurrentTasks() - 1);
        else
            currentParallelismDegree.setCurrentTasks(0);

        lockRedisRepository.save(currentParallelismDegree);
        System.out.println("Step 6: Decreased current_tasks is now at :" + currentParallelismDegree.getCurrentTasks());

        // TODO WHEN TO DELETE THE TASK FROM POSTGRE DATABASE
        sender.sendTaskToTasksQueue(scheduler_trigger);
        System.out.println("Step 7: Send Trigger for Scheduler, so new Tasks can be send to Dispatcher");
    }
}
