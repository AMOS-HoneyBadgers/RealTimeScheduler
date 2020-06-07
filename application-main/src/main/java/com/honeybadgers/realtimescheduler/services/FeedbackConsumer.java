package com.honeybadgers.realtimescheduler.services;

import com.honeybadgers.communication.ICommunication;
import com.honeybadgers.models.RedisLock;
import com.honeybadgers.models.RedisTask;
import com.honeybadgers.realtimescheduler.repository.LockRedisRepository;
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

    @Value("${dispatcher.capacity}")
    String dispatcherCapacity;

    @Value("${dispatcher.capacity.id}")
    String dispatcherCapacityId;

    @RabbitListener(queues = "dispatch.feedback", containerFactory = "feedbackcontainerfactory")
    public void receiveFeedbackFromDispatcher(String message) throws InterruptedException {
        System.out.println("Received feedback from dispatcher '{}'" + message);
        System.out.println("Step 4: ");

        //Thread.sleep((long) Math.random() * ((10000 - 1000) + 1) + 1000);
        RedisLock capacity = lockRedisRepository.findById(dispatcherCapacityId).orElse(null);
        // if capacity can not be found, something went wrong in the startup of the scheduler
        if(capacity == null){
            throw new RuntimeException("capacity could not be found in database");
        }

        // Race condition TODO Transaction
        // When the scheduler receives Feedback, the increase capacity by value X
        capacity.setCapacity(capacity.getCapacity()+1);
        lockRedisRepository.save(capacity);

    }
}
