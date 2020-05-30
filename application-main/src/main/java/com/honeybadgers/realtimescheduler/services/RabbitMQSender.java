package com.honeybadgers.realtimescheduler.services;


import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQSender implements ICommunication {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${dispatch.rabbitmq.exchange}")
    private String exchange;

    @Value("${dispatch.rabbitmq.routingkey}")
    private String routingkey;

    public void sendTaskToDispatcher(String task){
        rabbitTemplate.convertAndSend(exchange, routingkey, task);
        System.out.println("Send msg = " + task);
        
    }

    // Mock for Dispatcher
    public void sendFeedbackToScheduler(String task){
        rabbitTemplate.convertAndSend(exchange, routingkey, task);
        System.out.println("Send msg = " + task);

    }

    @Override
    public void send(String task) {
        
    }
}
