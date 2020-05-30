package com.honeybadgers.realtimescheduler.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(MockitoJUnitRunner.class)
class RabbitMQSenderTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    private RabbitMQSender sender;

    @BeforeEach
    public void beforeEach() {
        MockitoAnnotations.initMocks(this);
        sender = new RabbitMQSender(rabbitTemplate);
    }


    @Test
    void sendTaskToDispatcher() {
        RabbitMQSender spy = Mockito.spy(sender);
        spy.sendTaskToDispatcher("task");
        Mockito.verify(rabbitTemplate).convertAndSend(Mockito.any(), Mockito.any(), (Object) Mockito.any());
    }

    @Test
    void sendFeedbackToScheduler() {
        RabbitMQSender spy = Mockito.spy(sender);
        spy.sendFeedbackToScheduler("feedback");
        Mockito.verify(rabbitTemplate).convertAndSend(Mockito.any(), Mockito.any(), (Object) Mockito.any());
    }
}