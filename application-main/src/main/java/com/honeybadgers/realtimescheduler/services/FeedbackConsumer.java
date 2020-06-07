package com.honeybadgers.realtimescheduler.services;

import com.honeybadgers.communication.ICommunication;
import com.honeybadgers.models.RedisLock;
import com.honeybadgers.models.RedisTask;
import com.honeybadgers.realtimescheduler.repository.LockRedisRepository;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@EnableRabbit
public class FeedbackConsumer {

    @Autowired
    public ICommunication sender;

    @Autowired
    public LockRedisRepository lockRedisRepository;

    @RabbitListener(queues = "dispatch.feedback", containerFactory = "feedbackcontainerfactory")
    public void receiveFeedbackFromDispatcher(String message) throws InterruptedException {
        System.out.println("Received feedback from dispatcher '{}'" + message);
        System.out.println("Step 4: ");

        //Thread.sleep((long) Math.random() * ((10000 - 1000) + 1) + 1000);
        RedisLock capacity = lockRedisRepository.findById("Capacity").orElse(null);
        if(capacity == null){
            throw new RuntimeException("capacity could not be found in database");
        }
    }
}
