package com.honeybadgers.realtimescheduler.services;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
public class RabbitMQReceiverTest {

    @Mock
    ICommunication sender;

    private RabbitMQReceiver receiver;

    @Before
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