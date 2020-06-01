package com.honeybadgers.realtimescheduler.services;

import com.honeybadgers.realtimescheduler.model.Task;
import com.honeybadgers.realtimescheduler.repository.TaskPostgresRepository;
import com.honeybadgers.realtimescheduler.repository.TaskRedisRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringJUnit4ClassRunner.class)
public class TaskServiceTest {

    @Mock
    private TaskPostgresRepository taskPostgresRepository;

    @Mock
    private TaskRedisRepository taskRedisRepository;

    private TaskService service;

    @Before
    public void beforeEach() {
        service = new TaskService(taskRedisRepository, taskPostgresRepository);
    }


    @Test
    public void testGetAllTasks() {
        TaskService spy = Mockito.spy(service);
        spy.getAllTasks();
        Mockito.verify(taskPostgresRepository).findAll();
    }

}