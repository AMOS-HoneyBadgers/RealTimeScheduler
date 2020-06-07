package com.honeybadgers.integrationtests;

import com.honeybadgers.communication.ICommunication;
import com.honeybadgers.realtimescheduler.RealtimeschedulerApplication;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.verifier.messaging.boot.AutoConfigureMessageVerifier;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = RealtimeschedulerApplication.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AutoConfigureMessageVerifier
public abstract class BaseTest {

    @Autowired
    private ICommunication sender;

    protected void sendTasksToTasksQueue() {
        sender.sendTaskToTasksQueue("id12312");
    }
}

