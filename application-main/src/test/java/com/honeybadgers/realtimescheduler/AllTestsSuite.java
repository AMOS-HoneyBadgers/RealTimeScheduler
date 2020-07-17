package com.honeybadgers.realtimescheduler;

import com.honeybadgers.realtimescheduler.services.RabbitMQSenderTest;
import com.honeybadgers.realtimescheduler.services.impl.*;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses(value = {
        GroupServiceTest.class,
        TaskServiceTest.class,
        RabbitMQSenderTest.class,
        SpringJpaContextIntegrationTest.class,
        SchedulerServiceTest.class,
        LockServiceTest.class,
        ConvertUtilsTest.class
})
public class AllTestsSuite {
}
