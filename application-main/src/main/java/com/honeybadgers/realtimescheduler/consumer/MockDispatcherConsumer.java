package com.honeybadgers.realtimescheduler.consumer;

import com.honeybadgers.communication.ICommunication;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@EnableRabbit
public class MockDispatcherConsumer {

    static final Logger logger = LogManager.getLogger(MockDispatcherConsumer.class);

    /**
     * This Class MockDispatcherConsumer is for testing purposes only, in productive environment, this has to be replaced!
     */
    @Autowired
    public ICommunication sender;

    @RabbitListener(queues="dispatch.queue", containerFactory = "dispatchcontainerfactory")
    public void receiveTaskFromSchedulerMockDispatcher(String message) throws InterruptedException {
        logger.info("Received message in Mock Dispatcher'{}'" + message);

        // Mock Feedback, sleep between 10 and 20 seconds until feedback is sent back to the dispatcher
        /*Thread.sleep((long) Math.random() * ((20000 - 10000) + 1) + 10000);

        // Send feedback back to scheduler
        sender.sendFeedbackToScheduler(message);*/
    }
}
