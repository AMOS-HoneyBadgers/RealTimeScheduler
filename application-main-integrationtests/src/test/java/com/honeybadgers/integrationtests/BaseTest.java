package com.honeybadgers.integrationtests;


import com.honeybadgers.communication.ICommunication;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.verifier.messaging.boot.AutoConfigureMessageVerifier;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)

@SpringBootTest
@AutoConfigureMessageVerifier
public abstract class BaseTest {

    @Autowired
    private ICommunication sender;

    protected void testsendTasksToTasksQueue() {
        sender.sendTaskToTasksQueue("id12312");
    }
}

