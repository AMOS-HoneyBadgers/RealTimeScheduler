package com.honeybadgers.realtimescheduler.services;


import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQSender implements ICommunication {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${dispatch.rabbitmq.dispatcherexchange}")
    private String dispatcherexchange;

    @Value("${dispatch.rabbitmq.dispatcherroutingkey}")
    private String dispatcherroutingkey;

    @Value("${dispatch.rabbitmq.feedbackroutingkey}")
    private String feedbackroutingkey;

    @Value("${dispatch.rabbitmq.feedbackexchange}")
    String feedbackExchange;

    @Override
    public void sendTaskToDispatcher(String task) {
        rabbitTemplate.convertAndSend(dispatcherexchange, dispatcherroutingkey, task);
        System.out.println("Send msg = " + task);
    }

    // Mock for Dispatcher
    @Override
    public void sendFeedbackToScheduler(String feedback) {
        rabbitTemplate.convertAndSend(feedbackExchange, feedbackroutingkey, feedback);
        System.out.println("Send msg = " + feedback);
    }


}
