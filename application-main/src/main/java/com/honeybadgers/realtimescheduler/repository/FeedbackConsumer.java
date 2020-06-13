package com.honeybadgers.realtimescheduler.repository;

import com.honeybadgers.communication.ICommunication;
import com.honeybadgers.models.RedisLock;
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

    @RabbitListener(queues = "dispatch.feedback", containerFactory = "feedbackcontainerfactory")
    public void receiveFeedbackFromDispatcher(String message) throws InterruptedException {
        System.out.println("Step 5: Received feedback from dispatcher");

        /*RedisLock capacity = lockRedisRepository.findById(dispatcherCapacityId).orElse(null);
        // if capacity can not be found, something went wrong in the startup of the scheduler
        if(capacity == null){
            throw new RuntimeException("capacity could not be found in database");
        }*

        // Race condition TODO Transaction
        // When the scheduler receives Feedback, the increase capacity by 1
        capacity.setCurrentTasks(capacity.getCurrentTasks()+1);
        lockRedisRepository.save(capacity);
        System.out.println("Step 6: Increased capacity is now at :" + capacity.getCurrentTasks());*/


        // TODO WHEN TO DELETE THE TASK FROM POSTGRE DATABASE
        // TODO send Event to Scheduler, so the workflow of scheduling etc. is beeing triggered in a new QUEUE atm just workaround
        //sender.sendTaskToTasksQueue(scheduler_trigger);
        System.out.println("Step 7: Send Trigger for Scheduler, so new Tasks can be send to Dispatcher");
    }
}
