package com.honeybadgers.groupapi;

import com.honeybadgers.groupapi.controllers.DefaultApiControllerTest;
import com.honeybadgers.groupapi.service.GroupServiceTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses(value = {
        GroupServiceTest.class,
        DefaultApiControllerTest.class,
        SpringJpaContextIntegrationTest.class
})
public class AllTestsSuite {
}
