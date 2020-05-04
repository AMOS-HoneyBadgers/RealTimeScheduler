package com.honeybadgers.realtimescheduler.services;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        UserServiceTest.class,
        PostgresExampleServiceTest.class
})
public class ServiceTestSuite {
}
