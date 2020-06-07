package com.honeybadgers.realtimescheduler.integration;

import com.honeybadgers.realtimescheduler.services.TaskConsumerRabbit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.StubTrigger;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AutoConfigureStubRunner(stubsMode = StubRunnerProperties.StubsMode.LOCAL, ids = "com.honeybadgers:application-main-integrationtests")
@DirtiesContext
public class ListenerTest {

    @Autowired
    StubTrigger stubTrigger;

    @Autowired
    TaskConsumerRabbit service;

    @Test
    public void verifyBookOrderedEventContract() throws Exception {
        stubTrigger.trigger("task-push");
        assertThat(service.count > 0);
    }

}
