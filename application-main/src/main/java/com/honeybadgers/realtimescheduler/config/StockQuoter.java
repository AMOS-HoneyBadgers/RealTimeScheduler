package com.honeybadgers.realtimescheduler.config;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.Session;

import com.rabbitmq.jms.admin.RMQDestination;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.rabbitmq.jms.admin.RMQConnectionFactory;

@Configuration
@ComponentScan
@EnableAutoConfiguration
@EnableScheduling
public class StockQuoter {

    private static final Log log = LogFactory.getLog(StockQuoter.class);

    @Bean
    public JmsTemplate jmsTemplate() {
        JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setDefaultDestination(jmsDestination());
        return jmsTemplate;
    }

    @Bean()
    public Destination jmsDestination() {
        RMQDestination jmsDestination = new RMQDestination();
        jmsDestination.setDestinationName("dispatch.queue");
        jmsDestination.setAmqp(true);
        jmsDestination.setAmqpQueueName("${dispatch.rabbitmq.queue}");
        jmsDestination.setAmqpExchangeName("${dispatch.rabbitmq.exchange}");
        jmsDestination.setAmqpRoutingKey("${dispatch.rabbitmq.routingkey}");
        return jmsDestination;
    }

    @Bean
    ConnectionFactory connectionFactory() {
        return new RMQConnectionFactory();
    }

    @Scheduled(fixedRate = 5000L) // every 5 seconds
    public void publishQuote() {
        // Coerce a javax.jms.MessageCreator
        MessageCreator messageCreator = (Session session) -> {
            return session.createObjectMessage(
                    "asdasd");
        };
        // And publish to RabbitMQ using Spring's JmsTemplate
        jmsTemplate().send("dispatch.queue", messageCreator);
    }

    public static void main(String[] args) {
        SpringApplication.run(StockQuoter.class, args);
    }
}
