package com.honeybadgers.realtimescheduler.web;

import com.honeybadgers.realtimescheduler.services.RabbitMQSender;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.web.bind.annotation.*;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class HelloWorldController {

    static final Logger logger = LogManager.getLogger(HelloWorldController.class);

    @GetMapping("/hello")
    public String getHealth() {
        logger.info("Test info");
        logger.debug("Test debug");
        logger.warn("Test warn");
        logger.error("Test error");
        return "Hello World";
    }

    @GetMapping("/rabbit")
    public String getRabbit() {

        return "Send Rabbit";
    }
    /*@GetMapping("/error")
    public ResponseEntity<?> getError() {
        return ResponseEntity.badRequest().build();
    }

    @RequestMapping(value = "/hello/{name}", method = RequestMethod.GET)
    public ResponseEntity<String> helloUser(@PathVariable String name) {
        return ResponseEntity.ok().header("Test", "Test").body("Hello " + name);
    }

    @GetMapping("/param")
    public ResponseEntity<String> helloUserRequestParams(@RequestParam(required = false) String name) {
        return ResponseEntity.ok("Hello " + name);
    }

    @PostMapping("/logging")
    public ResponseEntity<?> loggingBody(@RequestBody String logBody) {
        log.info(logBody);
        return ResponseEntity.accepted().build();
    }*/

}
