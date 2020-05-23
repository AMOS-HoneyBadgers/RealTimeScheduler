package com.honeybadgers.realtimescheduler.web;

import org.apache.tomcat.util.threads.TaskQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import static com.honeybadgers.realtimescheduler.config.RmqConfig.TASK_QUEUE;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class JmsController {

    @Autowired
    JmsTemplate jmsTemplate;

    @GetMapping("/jms")
    public String getJms() {

        MessageCreator messageCreator = new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                return session.createObjectMessage("hello");
            }
        };
        jmsTemplate.send(messageCreator);
        return "tried to send";
    }
}
