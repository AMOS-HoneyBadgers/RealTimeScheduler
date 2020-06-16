package com.honeybadgers.managementapi;

import com.honeybadgers.managementapi.controllers.GroupApiControllerTest;
import com.honeybadgers.managementapi.controllers.SchedulerApiControllerTest;
import com.honeybadgers.managementapi.controllers.TaskApiControllerTest;
import com.honeybadgers.managementapi.service.ManagementServiceTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses(value = {
        /*GroupApiControllerTest.class,
        SchedulerApiControllerTest.class,
        TaskApiControllerTest.class,*/
        ManagementServiceTest.class
})
public class AllTestsSuite {
}
