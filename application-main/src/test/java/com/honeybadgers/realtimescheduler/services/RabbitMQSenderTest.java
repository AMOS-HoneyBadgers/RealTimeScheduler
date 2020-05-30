package com.honeybadgers.realtimescheduler.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

class RabbitMQSenderTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    private RabbitMQSender sender;

    @BeforeEach
    public void beforeEach() {
        MockitoAnnotations.initMocks(this);
        sender = new RabbitMQSender();
    }


    @Test
    void sendTaskToDispatcher() {
        RabbitMQSender spy = Mockito.spy(sender);
        spy.sendTaskToDispatcher("task");
        //Mockito.verify(rabbitTemplate.)
    }

    @Test
    void sendFeedbackToScheduler() {
    }
}