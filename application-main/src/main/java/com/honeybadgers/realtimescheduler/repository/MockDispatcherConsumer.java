package com.honeybadgers.realtimescheduler.repository;

import com.honeybadgers.communication.ICommunication;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@EnableRabbit
public class MockDispatcherConsumer {

    @Autowired
    public ICommunication sender;

    @RabbitListener(queues="dispatch.queue", containerFactory = "dispatchcontainerfactory")
    public void receiveTaskFromSchedulerMockDispatcher(String message) throws InterruptedException {
        System.out.println("Received message in Mock Dispatcher'{}'" + message);

        // Mock Feedback, sleep between 1 and 10 seconds until feedback is sent back to the dispatcher
        Thread.sleep((long) Math.random() * ((10000 - 1000) + 1) + 1000);

        // Send feedback back to scheduler
        //sender.sendFeedbackToScheduler(message);
    }
}
