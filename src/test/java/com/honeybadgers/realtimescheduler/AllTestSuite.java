package com.honeybadgers.realtimescheduler;

import com.honeybadgers.realtimescheduler.respository.RepositoryTestSuite;
import com.honeybadgers.realtimescheduler.services.ServiceTestSuite;
import com.honeybadgers.realtimescheduler.web.WebTestSuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        WebTestSuite.class,
        ServiceTestSuite.class,
        RepositoryTestSuite.class
})
public class AllTestSuite {
}
