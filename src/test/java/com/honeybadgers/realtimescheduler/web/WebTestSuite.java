package com.honeybadgers.realtimescheduler.web;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        TaskControllerTest.class,
        UserControllerTest.class
})
public class WebTestSuite {
}
