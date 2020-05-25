package com.honeybadgers.realtimescheduler.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
public class StockConsumer {


    /*@Bean
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
    }*/
}
