package com.honeybadgers.realtimescheduler;

import com.honeybadgers.realtimescheduler.repository.TaskPostgresRepositoryTest;
import com.honeybadgers.realtimescheduler.services.GroupServiceTest;
import com.honeybadgers.realtimescheduler.services.RabbitMQSenderTest;
import com.honeybadgers.realtimescheduler.services.TaskServiceTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses(value = {
        TaskPostgresRepositoryTest.class,
        GroupServiceTest.class,
        TaskServiceTest.class,
        RabbitMQSenderTest.class,
        SpringJpaContextIntegrationTest.class
})
public class AllTestsSuite {
}
