package com.honeybadgers.realtimescheduler.consumer;

import com.honeybadgers.communication.ICommunication;
import com.honeybadgers.postgre.repository.LockRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
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

    @Autowired
    LockRepository lockRepository;

    /**
     * Mocked the dispatcher in the scheduler instance. This method is called when a scheduler sends a task to the dispatcher
     * Sends feedback back to scheduler. In production there should be a dispatcher which should replace this
     * @param message task id which is received
     */
    @RabbitListener(queues="dispatch.queue", containerFactory = "dispatchcontainerfactory")
    public void receiveTaskFromSchedulerMockDispatcher(String message) {
        logger.info("Received message in Mock Dispatcher'{}'" + message);

        // This is for checking, whereas tasks are dispatched multiple times
        try {
            lockRepository.insert(message);
        } catch (DataIntegrityViolationException e) {
            logger.error("##############################################################################");
            logger.error("DataIntegrityViolationException for " + message);
            logger.error("##############################################################################");
        }

        // Send feedback back to scheduler
        //sender.sendFeedbackToScheduler(message);
    }
}
