package com.honeybadgers.communication;


import com.honeybadgers.communication.ICommunication;
import com.honeybadgers.communication.model.TaskQueueModel;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class RabbitMQSender implements ICommunication {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @PostConstruct
    private void setMessageConverter(){
        rabbitTemplate.setMessageConverter( new Jackson2JsonMessageConverter());
    }

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

    @Override
    public void sendTaskToDispatcher(String task) {
        rabbitTemplate.convertAndSend(dispatcherexchange, dispatcherroutingkey, task);
        System.out.println("Send task to dispatcher + " + task);
    }

    public String sendFeedbackToScheduler(String feedback) {
        rabbitTemplate.convertAndSend(feedbackExchange, feedbackroutingkey, feedback);
        System.out.println("Send feedback to Scheduler = " + feedback);
        return "test";
    }

    @Override
    public void sendTaskToTasksQueue(String task) {
        rabbitTemplate.convertAndSend(tasksExchange, tasksroutingkey, task);
        System.out.println("Send task to Taskqueue= " + task);
    }

    @Override
    public void sendGroupToTasksQueue(String group) {
        rabbitTemplate.convertAndSend(tasksExchange, tasksroutingkey, group);
        System.out.println("Send group Taskqueue= " + group);
    }

    @Override
    public void sendTaskToPriorityQueue(TaskQueueModel task) {
        rabbitTemplate.convertAndSend(priorityExchange, priorityroutingkey, task);
        System.out.println("Send task to Priorityqueue= " + task.toString());
    }
}
