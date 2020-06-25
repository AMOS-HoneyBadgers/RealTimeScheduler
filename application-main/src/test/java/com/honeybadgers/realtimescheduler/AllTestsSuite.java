package com.honeybadgers.realtimescheduler;

import com.honeybadgers.realtimescheduler.services.RabbitMQSenderTest;
import com.honeybadgers.realtimescheduler.services.impl.GroupServiceTest;
import com.honeybadgers.realtimescheduler.services.impl.SchedulerServiceTest;
import com.honeybadgers.realtimescheduler.services.impl.TaskServiceTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses(value = {
        GroupServiceTest.class,
        TaskServiceTest.class,
        RabbitMQSenderTest.class,
        SpringJpaContextIntegrationTest.class,
        SchedulerServiceTest.class
})
public class AllTestsSuite {
}
