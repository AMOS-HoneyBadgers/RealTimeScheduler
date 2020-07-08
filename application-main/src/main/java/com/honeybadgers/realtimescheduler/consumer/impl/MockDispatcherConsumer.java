package com.honeybadgers.realtimescheduler.consumer.impl;

import com.honeybadgers.communication.ICommunication;
import com.honeybadgers.realtimescheduler.consumer.IMockDispatcherConsumer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

@Component
@EnableRabbit
public class MockDispatcherConsumer implements IMockDispatcherConsumer {

    static final Logger logger = LogManager.getLogger(MockDispatcherConsumer.class);

    /**
     * This Class MockDispatcherConsumer is for testing purposes only, in productive environment, this has to be replaced!
     */
    @Autowired
    public ICommunication sender;



    @Override
    @RabbitListener(queues="dispatch.queue", containerFactory = "dispatchcontainerfactory")
    public void receiveTaskFromSchedulerMockDispatcher(String message) {
        logger.info("Received message in Mock Dispatcher'{}'" + message);



        // Send feedback back to scheduler
        sender.sendFeedbackToScheduler(message);
    }
}
