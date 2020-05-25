package com.honeybadgers.realtimescheduler.config;

import com.rabbitmq.jms.admin.RMQConnectionFactory;
import com.rabbitmq.jms.admin.RMQDestination;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;

@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackages = "com.honeybadgers.realtimescheduler")
@EnableJms
public class RmqConfig {

    public static final String TASK_QUEUE = "${dispatch.rabbitmq.queue}";

    @Bean
    public JmsListenerContainerFactory<?> myFactory(@Qualifier("mqConnection") ConnectionFactory connectionFactory,
                                                    DefaultJmsListenerContainerFactoryConfigurer configurer) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        // This provides all boot's default to this factory, including the message converter
        configurer.configure(factory, connectionFactory);
        // You could still override some of Boot's default if necessary.
        return factory;
    }

    @Bean
    public JmsTemplate jmsTemplate(@Qualifier("mqConnection") ConnectionFactory connectionFactory, Destination jmsDestination) {
        JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setConnectionFactory(connectionFactory);
        jmsTemplate.setDefaultDestination(jmsDestination);
        return jmsTemplate;
    }

    @Bean
    @Qualifier("mqConnection")
    public ConnectionFactory jmsConnectionFactory() {
        RMQConnectionFactory connectionFactory = new RMQConnectionFactory();
        connectionFactory.setUsername("${spring.rabbitmq.username}");
        connectionFactory.setPassword("${spring.rabbitmq.password}");
        connectionFactory.setVirtualHost("/");
        connectionFactory.setHost("${spring.rabbitmq.host}");
        connectionFactory.setPort(8883);
        return connectionFactory;
    }

    @Bean()
    public Destination jmsDestination() {
        RMQDestination jmsDestination = new RMQDestination();
        jmsDestination.setDestinationName("mydest");
        jmsDestination.setAmqp(true);
        jmsDestination.setAmqpQueueName("${dispatch.rabbitmq.queue}");
        jmsDestination.setAmqpExchangeName("${dispatch.rabbitmq.exchange}");
        jmsDestination.setAmqpRoutingKey("${dispatch.rabbitmq.routingkey}");
        return jmsDestination;
    }
}
