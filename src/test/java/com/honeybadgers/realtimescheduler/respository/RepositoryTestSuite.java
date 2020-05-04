package com.honeybadgers.realtimescheduler.respository;

import com.honeybadgers.realtimescheduler.respository.jpa.TaskPostgresRepositoryTest;
import com.honeybadgers.realtimescheduler.respository.redis.UserRedisRepositoryTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        TaskPostgresRepositoryTest.class,
        UserRedisRepositoryTest.class
})
public class RepositoryTestSuite {
}
