package com.honeybadgers.groupapi.service.impl;



import com.honeybadgers.groupapi.service.ISendGroupsToTaksQueue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQSender implements ISendGroupsToTaksQueue {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${dispatch.rabbitmq.tasksroutingkey}")
    private String tasksroutingkey;


    @Value("${dispatch.rabbitmq.tasksexchange}")
    String tasksExchange;

    public RabbitMQSender(RabbitTemplate template) {
        this.rabbitTemplate = template;
    }


    @Override
    public void sendGroupToTasksQueue(String task) {
        rabbitTemplate.convertAndSend(tasksExchange, tasksroutingkey, task);
        System.out.println("Send msg = " + task);
    }
}
