package com.honeybadgers.realtimescheduler.config;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;

import com.rabbitmq.jms.admin.RMQDestination;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.jms.listener.adapter.MessageListenerAdapter;

@Configuration
@ComponentScan
public class StockConsumer {


    @Bean
    public DefaultMessageListenerContainer jmsListener(ConnectionFactory connectionFactory) {
        DefaultMessageListenerContainer jmsListener = new DefaultMessageListenerContainer();
        jmsListener.setConnectionFactory(connectionFactory);
        jmsListener.setDestinationName("dispatch.queue");
        jmsListener.setPubSubDomain(false);

        MessageListenerAdapter adapter = new MessageListenerAdapter(new Receiver());
        adapter.setDefaultListenerMethod("receive");

        jmsListener.setMessageListener(adapter);
        return jmsListener;
    }



    static class Receiver {
        public void receive(String message) {
            System.out.println("Received " + message);
        }
    }
}
