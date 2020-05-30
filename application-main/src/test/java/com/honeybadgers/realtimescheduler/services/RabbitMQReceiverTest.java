package com.honeybadgers.realtimescheduler.services;

import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
class RabbitMQReceiverTest {

    @Mock
    ICommunication sender;

    private RabbitMQReceiver receiver;

    @BeforeEach
    public void beforeEach() {
        MockitoAnnotations.initMocks(this);
        receiver = new RabbitMQReceiver(sender);
    }

    @Test
    public void testReceiveTaskCallsInternalMethods() throws InterruptedException {
        RabbitMQReceiver spy = Mockito.spy(receiver);
        spy.receiveTask("ss");
        Mockito.verify(spy).receiveTask(Mockito.any());
        Mockito.verify(spy).workTask();
    }


    @Test
    public void testReceiveFeedbackCallsInternalMethods() throws InterruptedException {
        RabbitMQReceiver spy = Mockito.spy(receiver);
        spy.receiveFeedback("ss");
        Mockito.verify(spy).receiveFeedback(Mockito.any());
        Mockito.verify(spy).changeTaskStatus(Mockito.any());
    }
}