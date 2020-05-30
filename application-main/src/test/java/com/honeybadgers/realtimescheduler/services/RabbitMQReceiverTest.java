package com.honeybadgers.realtimescheduler.services;

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

@RunWith(MockitoJUnitRunner.class)
class RabbitMQReceiverTest {
    @Bean
    public ICommunication sender(){
        return Mockito.mock(ICommunication.class);
    };
    @Autowired
    ICommunication sender;


   /* @Test
    void receiveTaskWithRealObjectFromRabbitMQReceiver() throws InterruptedException {
        MockComm mockComm = new MockComm();
        RabbitMQReceiver rabbitMQReceiverSpy = Mockito.spy(new RabbitMQReceiver(mockComm));
        rabbitMQReceiverSpy.receiveTask("test");
        Mockito.verify(rabbitMQReceiverSpy).receiveTask(Mockito.anyString());
        Mockito.verify(rabbitMQReceiverSpy).workTask();
    }*/

   /* @Test
    void receiveTaskWithMockFromRabbitMQReceiver() throws InterruptedException {
        RabbitMQReceiver rabbitMQReceiverSpy = Mockito.mock(RabbitMQReceiver.class);
        Mockito.when(sender.sendFeedbackToScheduler(Mockito.anyString())).thenReturn("test");
        rabbitMQReceiverSpy.receiveTask("test");
        Mockito.verify(rabbitMQReceiverSpy).receiveTask(Mockito.anyString());
        Mockito.verify(rabbitMQReceiverSpy).workTask();
    }*/

    @Test
    void receiveFeedback() {
    }

    /*class MockComm implements ICommunication {

        @Override
        public void sendTaskToDispatcher(String task) {
            System.out.println("got" + task);
        }

        @Override
        public String sendFeedbackToScheduler(String feedback) {
            System.out.println("got" + feedback);
        }
    }*/
}