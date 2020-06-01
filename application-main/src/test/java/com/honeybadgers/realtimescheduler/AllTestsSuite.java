package com.honeybadgers.realtimescheduler;

import com.honeybadgers.realtimescheduler.repository.TaskPostgresRepositoryTest;
import com.honeybadgers.realtimescheduler.services.GroupServiceTest;
import com.honeybadgers.realtimescheduler.services.RabbitMQReceiverTest;
import com.honeybadgers.realtimescheduler.services.RabbitMQSenderTest;
import com.honeybadgers.realtimescheduler.services.TaskServiceTest;
import com.honeybadgers.realtimescheduler.web.HelloWorldControllerTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses(value = {
        TaskPostgresRepositoryTest.class,
        GroupServiceTest.class,
        TaskServiceTest.class,
        RabbitMQReceiverTest.class,
        RabbitMQSenderTest.class,
        HelloWorldControllerTest.class,
        SpringJpaContextIntegrationTest.class
})
public class AllTestsSuite {
}
