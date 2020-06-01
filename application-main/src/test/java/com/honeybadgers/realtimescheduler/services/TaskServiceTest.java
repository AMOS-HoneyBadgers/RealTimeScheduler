package com.honeybadgers.realtimescheduler.services;


import com.honeybadgers.models.Task;
import com.honeybadgers.realtimescheduler.model.RedisTask;
import com.honeybadgers.realtimescheduler.repository.TaskPostgresRepository;
import com.honeybadgers.realtimescheduler.repository.TaskRedisRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

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
        TaskService spy = spy(service);
        spy.getAllTasks();
        verify(taskPostgresRepository).findAll();
    }

    @Test
    public void testUploadTask() {
        TaskService spy = spy(service);
        spy.uploadTask(any());
        verify(taskPostgresRepository).save(Mockito.any());
    }

    @Test
    public void testDeleteTaskWithCorrectId() {
        String id = "123";
        TaskService spy = spy(service);
        spy.deleteTask(id);
        verify(taskPostgresRepository).deleteById(id);
    }

    @Test
    public void testcalculatePriorityCreatesARealRedisTaskObject() {
        Task task = new Task();
        task.setId("123");
        RedisTask verify = service.calculatePriority(task);
        Assert.assertEquals(verify.getId(), "123");
        Assert.assertNotNull(verify.getPriority());
    }

    @Test
    public void testScheduleTask() {
        TaskService spy = spy(service);
        spy.scheduleTask(any());
        verify(taskRedisRepository).save(any());
    }

    @Test
    public void testgetAllTasks2() {
        List<Task> tasks = new ArrayList<Task>();
        for (int i = 1; i < 4; i++) {
            Task t = new Task();
            t.setId(String.valueOf(i));
            tasks.add(t);
        }
        Mockito.when(taskPostgresRepository.findAll()).thenReturn(tasks);
        List<Task> returnedTasks = service.getAllTasks();
        Assert.assertEquals(tasks, returnedTasks);
    }
  
}

