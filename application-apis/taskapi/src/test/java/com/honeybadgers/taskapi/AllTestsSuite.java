package com.honeybadgers.taskapi;

import com.honeybadgers.taskapi.controllers.DefaultApiControllerTest;
import com.honeybadgers.taskapi.controllers.TaskIdApiControllerTest;
import com.honeybadgers.taskapi.service.TaskServiceTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses(value = {
        TaskServiceTest.class,
        DefaultApiControllerTest.class,
        TaskIdApiControllerTest.class,
        SpringJpaContextIntegrationTest.class
})
public class AllTestsSuite {
}
