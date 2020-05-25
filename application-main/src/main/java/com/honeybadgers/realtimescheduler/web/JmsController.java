package com.honeybadgers.realtimescheduler.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class JmsController {

    @Autowired
    JmsTemplate jmsTemplate;

    @GetMapping("/jms")
    public String getJms() {

        /*MessageCreator messageCreator = new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                return session.createObjectMessage("hello");
            }
        };*/
        jmsTemplate.convertAndSend("hellooooooooojms");
        return "tried to send";
    }
}
