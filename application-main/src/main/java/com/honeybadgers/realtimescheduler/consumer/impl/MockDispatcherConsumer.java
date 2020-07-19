package com.honeybadgers.realtimescheduler.consumer.impl;

import com.honeybadgers.communication.ICommunication;
import com.honeybadgers.communication.model.TaskQueueModel;
import com.honeybadgers.realtimescheduler.consumer.IMockDispatcherConsumer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
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
    @RabbitListener(queues = "dispatch.queue", containerFactory = "dispatchcontainerfactory")
    public void receiveTaskFromSchedulerMockDispatcher(TaskQueueModel task) {
        logger.info("Received task in Mock Dispatcher for task with id " + task.getId());

        // Send feedback back to scheduler
        sender.sendFeedbackToScheduler(task.getId());
    }
}
