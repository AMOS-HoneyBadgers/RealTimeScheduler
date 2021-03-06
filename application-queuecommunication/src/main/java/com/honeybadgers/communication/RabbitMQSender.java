package com.honeybadgers.communication;


import com.honeybadgers.communication.model.TaskQueueModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQSender implements ICommunication {

    static final Logger logger = LogManager.getLogger(RabbitMQSender.class);

    @Autowired
    private final RabbitTemplate rabbitTemplate;

    @Value("${dispatch.rabbitmq.dispatcherexchange}")
    private String dispatcherexchange;

    @Value("${dispatch.rabbitmq.dispatcherroutingkey}")
    private String dispatcherroutingkey;

    @Value("${dispatch.rabbitmq.feedbackroutingkey}")
    private String feedbackroutingkey;

    @Value("${dispatch.rabbitmq.priorityroutingkey}")
    private String priorityroutingkey;

    @Value("${dispatch.rabbitmq.tasksroutingkey}")
    private String tasksroutingkey;

    @Value("${dispatch.rabbitmq.feedbackexchange}")
    String feedbackExchange;

    @Value("${dispatch.rabbitmq.tasksexchange}")
    String tasksExchange;

    @Value("${dispatch.rabbitmq.priorityexchange}")
    String priorityExchange;

    public RabbitMQSender(RabbitTemplate template) {
        this.rabbitTemplate = template;
    }

    /**
     * This Method is for testing purposes only, in productive environment, this has to be replaced with real Dispatcher Queue
     */
    @Override
    public void sendTaskToDispatcher(TaskQueueModel task) {
        rabbitTemplate.convertAndSend(dispatcherexchange, dispatcherroutingkey, task);
        logger.debug("Task " + task + " sent to dispatcher");
    }

    @Override
    public String sendFeedbackToScheduler(String feedback) {
        rabbitTemplate.convertAndSend(feedbackExchange, feedbackroutingkey, feedback);
        logger.debug("Feedback for Task " + feedback + " sent to Scheduler");
        return "test";
    }

    @Override
    public void sendTaskToTasksQueue(String task) {
        rabbitTemplate.convertAndSend(tasksExchange, tasksroutingkey, task);
        logger.debug("Task " + task + " sent to Taskqueue");
    }

    @Override
    public void sendGroupToTasksQueue(String group) {
        rabbitTemplate.convertAndSend(tasksExchange, tasksroutingkey, group);
        logger.debug("Group " + group + " sent Taskqueue");
    }

    @Override
    public void sendTaskToPriorityQueue(TaskQueueModel task) {
        rabbitTemplate.convertAndSend(priorityExchange, priorityroutingkey, task);
        logger.debug("Task " + task.toString() + " send to Priorityqueue");
    }
}
