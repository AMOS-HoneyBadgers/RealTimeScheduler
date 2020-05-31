package com.honeybadgers.realtimescheduler.services;

import com.honeybadgers.models.Task;
import com.honeybadgers.realtimescheduler.repository.TaskPostgresRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TaskServiceTest {

    @Mock
    TaskPostgresRepository taskPostgresRepositoryMock;
    @InjectMocks
    private TaskService taskService;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void getAllTasks() {
        List<Task> tasks = new ArrayList<Task>();
        for (int i = 1; i < 4; i++) {
            Task t = new Task();
            t.setId(String.valueOf(i));
            tasks.add(t);
        }
        Mockito.when(taskPostgresRepositoryMock.findAll()).thenReturn(tasks);
        List<Task> returnedTasks = taskService.getAllTasks();
        Assert.assertEquals(tasks, returnedTasks);
    }

    @Test
    void uploadTask() {
        Task task = new Task();
        task.setId("test");
        TaskService spy = Mockito.spy(taskService);
        spy.uploadTask(task);
        Mockito.verify(spy).uploadTask(task);
    }

    @Test
    void deleteTask() {
        TaskService spy = Mockito.spy(taskService);
        spy.deleteTask("task");
        Mockito.verify(spy).deleteTask("task");
    }

    @Test
    void calculatePriority() {
    }

    @Test
    void scheduleTask() {
    }
}