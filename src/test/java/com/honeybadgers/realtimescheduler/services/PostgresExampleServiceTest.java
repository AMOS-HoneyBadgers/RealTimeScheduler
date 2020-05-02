package com.honeybadgers.realtimescheduler.services;

import com.honeybadgers.realtimescheduler.domain.jpa.Task;
import com.honeybadgers.realtimescheduler.repository.jpa.TaskPostgresRepository;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Timestamp;
import java.util.Optional;

@RunWith(SpringRunner.class)
public class PostgresExampleServiceTest {

    @TestConfiguration
    static class PostgresExampleServiceTestContextConfiguration {

        @Bean
        public PostgresExampleService postgresExampleService() {
            return new PostgresExampleService();
        }
    }

    @Autowired
    private PostgresExampleService postgresExampleService;

    @MockBean
    private TaskPostgresRepository taskPostgresRepository;

    @Before
    public void setUp() {
        Task task = new Task(1L, 10, "testTask1", null, new Timestamp(System.currentTimeMillis()), null);

        Mockito.when(taskPostgresRepository.findById(1L))
                .thenReturn(Optional.of(task));
    }
}
