package com.honeybadgers.realtimescheduler.services;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;



@Service
public class RabbitMQSender {
    @Autowired
    private AmqpTemplate rabbitTemplate;

    @Value("${dispatch.rabbitmq.exchange}")
    private String exchange;

    @Value("${dispatch.rabbitmq.routingkey}")
    private String routingkey;

    //Task task) {
    public void send(String task){
        rabbitTemplate.convertAndSend(exchange, routingkey, task);
        System.out.println("Send msg = " + task);
        
    }
}
