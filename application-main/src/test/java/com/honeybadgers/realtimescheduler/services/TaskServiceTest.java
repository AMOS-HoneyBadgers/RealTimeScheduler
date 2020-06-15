package com.honeybadgers.realtimescheduler.services;

import com.honeybadgers.communication.ICommunication;
import com.honeybadgers.models.Task;
import com.honeybadgers.realtimescheduler.repository.TaskPostgresRepository;
import com.honeybadgers.realtimescheduler.repository.TaskRedisRepository;
import com.honeybadgers.realtimescheduler.services.impl.TaskService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = TaskService.class)
public class TaskServiceTest {

    @MockBean
    private TaskPostgresRepository taskPostgresRepository;

    @Autowired
    private TaskService service;

    @Value("${com.realtimescheduler.scheduler.priority.deadline-bonus-base-prio-dependant}")
    boolean deadlineBaseDependant;

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
    public void testGetTaskById() {
        TaskService spy = spy(service);
        spy.getTaskById("123");
        verify(taskPostgresRepository).findById("123");
    }

    @Test
    public void testCalculatePriority() {
        Task newTask = new Task();
        newTask.setId("TEST");
        newTask.setPriority(20);

        long prio = service.calculatePriority(newTask);

        // If all Modifier = 1 and const = 1000
        Assert.assertEquals(50, prio);

        // set deadline and check final priority
        newTask.setDeadline(new Timestamp(System.currentTimeMillis() + 100000));
        prio = service.calculatePriority(newTask);
        Assert.assertTrue(prio > 50);

        // create new Task with lower deadline and check final priority
        Task newTaskHigherPrio = new Task();
        newTaskHigherPrio.setId("TEST2");
        newTaskHigherPrio.setPriority(20);
        newTaskHigherPrio.setDeadline(new Timestamp(System.currentTimeMillis() + 50000));
        long higherPrio = service.calculatePriority(newTaskHigherPrio);
        Assert.assertTrue(higherPrio > prio);


    }

    @Test
    public void testGetAllTasks2() {
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

